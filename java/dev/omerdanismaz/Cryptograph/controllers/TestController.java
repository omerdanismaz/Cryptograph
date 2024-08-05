package dev.omerdanismaz.Cryptograph.controllers;

import dev.omerdanismaz.Cryptograph.enums.ECookieAttributes;
import dev.omerdanismaz.Cryptograph.enums.ESessionAttributes;
import dev.omerdanismaz.Cryptograph.iservices.ISessionService;
import dev.omerdanismaz.Cryptograph.iservices.IUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import static dev.omerdanismaz.Cryptograph.functions.CookieOperations.getCookie;
import static dev.omerdanismaz.Cryptograph.functions.SessionOperations.getSessionAttribute;
import static dev.omerdanismaz.Cryptograph.functions.SessionOperations.initializeSession;

@Controller
@AllArgsConstructor
public class TestController
{
    private final IUserService userService;
    private final ISessionService sessionService;

    @GetMapping("/test")
    public String testGET(Model model,
                          HttpServletRequest httpServletRequest,
                          HttpServletResponse httpServletResponse)
    {
        initializeSession(httpServletRequest, httpServletResponse, userService, sessionService);

        String cookieUserId = getCookie(httpServletRequest,
                ECookieAttributes.USER_ID);
        String cookieUserEncryptionKey = getCookie(httpServletRequest,
                ECookieAttributes.USER_ENCRYPTION_KEY);
        String cookieSessionToken = getCookie(httpServletRequest,
                ECookieAttributes.SESSION_TOKEN);
        String sessionUserId = getSessionAttribute(httpServletRequest,
                ESessionAttributes.USER_ID);
        String sessionUserFirstName = getSessionAttribute(httpServletRequest,
                ESessionAttributes.USER_FIRST_NAME);
        String sessionUserLastName = getSessionAttribute(httpServletRequest,
                ESessionAttributes.USER_LAST_NAME);
        String sessionUserEmail = getSessionAttribute(httpServletRequest,
                ESessionAttributes.USER_EMAIL);
        String sessionUserEncryptionKey = getSessionAttribute(httpServletRequest,
                ESessionAttributes.USER_ENCRYPTION_KEY);
        String sessionUserStatus = getSessionAttribute(httpServletRequest,
                ESessionAttributes.USER_STATUS);
        String sessionCSRFToken = getSessionAttribute(httpServletRequest,
                ESessionAttributes.CSRF_TOKEN);

        model.addAttribute("cookieUserId", cookieUserId);
        model.addAttribute("cookieUserEncryptionKey", cookieUserEncryptionKey);
        model.addAttribute("cookieSessionToken", cookieSessionToken);
        model.addAttribute("sessionUserId", sessionUserId);
        model.addAttribute("sessionUserFirstName", sessionUserFirstName);
        model.addAttribute("sessionUserLastName", sessionUserLastName);
        model.addAttribute("sessionUserEmail", sessionUserEmail);
        model.addAttribute("sessionUserEncryptionKey", sessionUserEncryptionKey);
        model.addAttribute("sessionUserStatus", sessionUserStatus);
        model.addAttribute("sessionCSRFToken", sessionCSRFToken);

        functionTestArea(httpServletRequest, httpServletResponse);

        return "/test/test";
    }

    private void functionTestArea(HttpServletRequest httpServletRequest,
                                  HttpServletResponse httpServletResponse)
    {

    }
}
