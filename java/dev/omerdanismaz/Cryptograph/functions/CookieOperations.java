package dev.omerdanismaz.Cryptograph.functions;

import dev.omerdanismaz.Cryptograph.enums.ECookieAttributes;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CookieOperations
{
    public static String getCookie(HttpServletRequest httpServletRequest,
                                   ECookieAttributes cookieAttribute)
    {
        Cookie[] allCookies = httpServletRequest.getCookies();

        if(allCookies != null)
        {
            for(Cookie cookie : allCookies)
            {
                if(cookie.getName().equals(cookieAttribute.name()))
                {
                    return cookie.getValue();
                }
            }
        }

        return "EMPTY";
    }

    public static void setCookie(HttpServletResponse httpServletResponse,
                                 ECookieAttributes cookieAttribute,
                                 String cookieValue)
    {
        Cookie cookie = new Cookie(cookieAttribute.name(), cookieValue);
        cookie.setPath("/");
        cookie.setMaxAge(30 * 24 * 60 * 60);
        httpServletResponse.addCookie(cookie);
    }
}
