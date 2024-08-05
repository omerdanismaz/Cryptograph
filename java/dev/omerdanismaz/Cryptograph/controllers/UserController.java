package dev.omerdanismaz.Cryptograph.controllers;

import dev.omerdanismaz.Cryptograph.enums.ECookieAttributes;
import dev.omerdanismaz.Cryptograph.enums.ECryptographyError;
import dev.omerdanismaz.Cryptograph.enums.ESessionAttributes;
import dev.omerdanismaz.Cryptograph.iservices.ISessionService;
import dev.omerdanismaz.Cryptograph.iservices.IUserService;
import dev.omerdanismaz.Cryptograph.models.UserModel;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

import static dev.omerdanismaz.Cryptograph.cryptography.CryptographyOperations.decryptData;
import static dev.omerdanismaz.Cryptograph.functions.CookieOperations.getCookie;
import static dev.omerdanismaz.Cryptograph.functions.GeneralOperations.*;
import static dev.omerdanismaz.Cryptograph.functions.SessionOperations.*;
import static dev.omerdanismaz.Cryptograph.functions.SessionOperations.invalidateSession;

@Controller
@AllArgsConstructor
public class UserController
{
    private final IUserService userService;
    private final ISessionService sessionService;

    @GetMapping("/register")
    public String registerGET(Model model,
                              HttpServletRequest httpServletRequest,
                              HttpServletResponse httpServletResponse)
    {
        initializeSession(httpServletRequest, httpServletResponse, userService, sessionService);

        if(!isUserGuest(httpServletRequest))
        {
            return "redirect:/";
        }

        model.addAttribute("CSRFToken",
                getSessionAttribute(httpServletRequest, ESessionAttributes.CSRF_TOKEN));
        model.addAttribute("messageOne",
                "First name field cannot be empty and can be up to 32 characters long.");
        model.addAttribute("messageTwo",
                "First name field contains invalid characters.");
        model.addAttribute("messageThree",
                "Last name field cannot be empty and can be up to 32 characters long.");
        model.addAttribute("messageFour",
                "Last name field contains invalid characters.");
        model.addAttribute("messageFive",
                "Email address field cannot be empty and can be up to 64 characters long.");
        model.addAttribute("messageSix",
                "Email address is invalid.");
        model.addAttribute("messageSeven",
                "Email address is already in use.");
        model.addAttribute("messageEight",
                "Password field cannot be empty and must be 8-32 characters long.");
        model.addAttribute("messageNine",
                "Passwords do not match.");

        return "/authentication/register";
    }

    @PostMapping("/register")
    public String registerPOST(@RequestParam("CSRFToken") String CSRFToken,
                               @RequestParam("firstName") String firstName,
                               @RequestParam("lastName") String lastName,
                               @RequestParam("email") String email,
                               @RequestParam("password") String password,
                               @RequestParam("confirmPassword") String confirmPassword,
                               HttpServletRequest httpServletRequest,
                               HttpServletResponse httpServletResponse)
    {
        if(!isUserGuest(httpServletRequest))
        {
            return "redirect:/";
        }

        if(!isCSRFTokenValid(httpServletRequest, CSRFToken))
        {
            return "redirect:/";
        }

        if(firstName.isEmpty() || firstName.length() > 32 || isNotAlphaUnicode(firstName))
        {
            return "redirect:/";
        }

        if(lastName.isEmpty() || lastName.length() > 32 || isNotAlphaUnicode(lastName))
        {
            return "redirect:/";
        }

        UserModel searchedUser = userService.readUserByEmail(email);
        String searchedEmail = searchedUser.getDbfUserEmail();

        if(email.isEmpty() || email.length() > 64 || isNotEmail(email)|| !searchedEmail.equals("EMPTY"))
        {
            return "redirect:/";
        }

        if(password.length() < 8 || password.length() > 32 || !password.equals(confirmPassword))
        {
            return "redirect:/";
        }

        UserModel userModel = UserModel.builder()
                .dbfUserFirstName(removeExtraSpaces(firstName))
                .dbfUserLastName(removeExtraSpaces(lastName))
                .dbfUserEmail(removeExtraSpaces(email))
                .dbfUserPassword(password)
                .build();
        userService.createUser(userModel);

        UserModel createdUser = userService.readUserByEmail(email);
        createdUser.setDbfUserPassword(password);

        buildSession(httpServletRequest, httpServletResponse, sessionService, createdUser);

        return "redirect:/";
    }

    @GetMapping("/login")
    public String loginGET(Model model,
                           HttpServletRequest httpServletRequest,
                           HttpServletResponse httpServletResponse)
    {
        initializeSession(httpServletRequest, httpServletResponse, userService, sessionService);

        if(!isUserGuest(httpServletRequest))
        {
            return "redirect:/";
        }

        model.addAttribute("CSRFToken",
                getSessionAttribute(httpServletRequest, ESessionAttributes.CSRF_TOKEN));
        model.addAttribute("messageOne",
                "Email address field cannot be empty and can be up to 64 characters long.");
        model.addAttribute("messageTwo",
                "Email address is invalid.");
        model.addAttribute("messageThree",
                "Password field cannot be empty and must be 8-32 characters long.");
        model.addAttribute("messageFour",
                "Email address or password is wrong.");

        return "/authentication/login";
    }

    @PostMapping("/login")
    public String loginPOST(@RequestParam("CSRFToken") String CSRFToken,
                            @RequestParam("email") String email,
                            @RequestParam("password") String password,
                            HttpServletRequest httpServletRequest,
                            HttpServletResponse httpServletResponse)
    {
        if(!isUserGuest(httpServletRequest))
        {
            return "redirect:/";
        }

        if(!isCSRFTokenValid(httpServletRequest, CSRFToken))
        {
            return "redirect:/";
        }

        UserModel searchedUser = userService.readUserByEmail(email);
        String searchedEmail = searchedUser.getDbfUserEmail();
        String searchedPasswordHash = searchedUser.getDbfUserPassword();

        if(email.isEmpty() || email.length() > 64 || isNotEmail(email) || searchedEmail.equals("EMPTY"))
        {
            return "redirect:/";
        }

        if(password.length() < 8 || password.length() > 32)
        {
            return "redirect:/";
        }

        if(decryptData(searchedPasswordHash, password).equals(ECryptographyError.CRYPTOGRAPHY_ERROR.name()))
        {
            return "redirect:/";
        }

        searchedUser.setDbfUserLoggedOn(new Date());
        userService.updateUser(searchedUser);
        searchedUser.setDbfUserPassword(password);

        buildSession(httpServletRequest, httpServletResponse, sessionService, searchedUser);

        return "redirect:/";
    }

    @GetMapping("/logout")
    private String logoutGET(HttpServletRequest httpServletRequest,
                             HttpServletResponse httpServletResponse)
    {
        initializeSession(httpServletRequest, httpServletResponse, userService, sessionService);

        String sessionToken = getCookie(httpServletRequest, ECookieAttributes.SESSION_TOKEN);
        sessionService.deleteSession(sessionService.readSessionByToken(sessionToken));

        invalidateSession(httpServletRequest, httpServletResponse);

        return "redirect:/login";
    }
}
