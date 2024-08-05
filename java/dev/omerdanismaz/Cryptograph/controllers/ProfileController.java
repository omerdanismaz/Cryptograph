package dev.omerdanismaz.Cryptograph.controllers;

import dev.omerdanismaz.Cryptograph.enums.ESessionAttributes;
import dev.omerdanismaz.Cryptograph.iservices.ISessionService;
import dev.omerdanismaz.Cryptograph.iservices.IUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static dev.omerdanismaz.Cryptograph.functions.SessionOperations.*;

@Controller
@AllArgsConstructor
public class ProfileController
{
    private final IUserService userService;
    private final ISessionService sessionService;

    @GetMapping("/profile")
    public String profileGET(Model model,
                             HttpServletRequest httpServletRequest,
                             HttpServletResponse httpServletResponse)
    {
        initializeSession(httpServletRequest, httpServletResponse, userService, sessionService);

        if(isUserGuest(httpServletRequest))
        {
            return "redirect:/login";
        }

        String sessionUserFirstName = getSessionAttribute(httpServletRequest, ESessionAttributes.USER_FIRST_NAME);
        String sessionUserLastName = getSessionAttribute(httpServletRequest, ESessionAttributes.USER_LAST_NAME);
        String sessionUserFullName = sessionUserFirstName + " " + sessionUserLastName;
        String sessionUserEmail = getSessionAttribute(httpServletRequest, ESessionAttributes.USER_EMAIL);

        model.addAttribute("CSRFToken",
                getSessionAttribute(httpServletRequest, ESessionAttributes.CSRF_TOKEN));
        model.addAttribute("sessionUserFullName", sessionUserFullName);
        model.addAttribute("sessionUserEmail", sessionUserEmail);

        return "/app/profile";
    }

    @PostMapping("/terminateAllSessions")
    public String terminateAllSessionsPOST(@RequestParam("CSRFToken") String CSRFToken,
                                           HttpServletRequest httpServletRequest)
    {
        if(isUserGuest(httpServletRequest))
        {
            return "redirect:/login";
        }

        if(!isCSRFTokenValid(httpServletRequest, CSRFToken))
        {
            return "redirect:/";
        }

        Long userId = Long.parseLong(getSessionAttribute(httpServletRequest, ESessionAttributes.USER_ID));
        sessionService.deleteAllUserSessions(userId);

        return "redirect:/login";
    }
}
