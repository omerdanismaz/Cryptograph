package dev.omerdanismaz.Cryptograph.functions;

import dev.omerdanismaz.Cryptograph.enums.ECookieAttributes;
import dev.omerdanismaz.Cryptograph.enums.ESessionAttributes;
import dev.omerdanismaz.Cryptograph.enums.EUserStatus;
import dev.omerdanismaz.Cryptograph.iservices.IAccountService;
import dev.omerdanismaz.Cryptograph.iservices.INoteService;
import dev.omerdanismaz.Cryptograph.iservices.ISessionService;
import dev.omerdanismaz.Cryptograph.iservices.IUserService;
import dev.omerdanismaz.Cryptograph.models.AccountModel;
import dev.omerdanismaz.Cryptograph.models.NoteModel;
import dev.omerdanismaz.Cryptograph.models.SessionModel;
import dev.omerdanismaz.Cryptograph.models.UserModel;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

import static dev.omerdanismaz.Cryptograph.cryptography.CryptographyOperations.decryptData;
import static dev.omerdanismaz.Cryptograph.functions.CookieOperations.getCookie;
import static dev.omerdanismaz.Cryptograph.functions.CookieOperations.setCookie;
import static dev.omerdanismaz.Cryptograph.functions.GeneralOperations.generateRandomString;
import static dev.omerdanismaz.Cryptograph.functions.GeneralOperations.initializeExpirationDate;

public class SessionOperations
{
    public static void initializeSession(HttpServletRequest httpServletRequest,
                                         HttpServletResponse httpServletResponse,
                                         IUserService userService,
                                         ISessionService sessionService)
    {
        String cookieUserId = getCookie(httpServletRequest, ECookieAttributes.USER_ID);
        String cookieSessionToken = getCookie(httpServletRequest, ECookieAttributes.SESSION_TOKEN);
        String sessionUserStatus = getSessionAttribute(httpServletRequest, ESessionAttributes.USER_STATUS);

        SessionModel searchedSession = sessionService.readSessionByToken(cookieSessionToken);

        if(searchedSession.getDbfSessionToken().equals("EMPTY")
                || sessionService.isSessionExpired(searchedSession))
        {
            invalidateSession(httpServletRequest, httpServletResponse);
        }

        if(sessionUserStatus.equals("EMPTY"))
        {
            setSessionAttribute(httpServletRequest, ESessionAttributes.CSRF_TOKEN, generateRandomString(32));
            setSessionAttribute(httpServletRequest, ESessionAttributes.USER_STATUS, EUserStatus.GUEST.name());
        }

        if(!cookieUserId.equals("EMPTY"))
        {
            String sessionUserId = getSessionAttribute(httpServletRequest, ESessionAttributes.USER_ID);

            if(!sessionUserId.equals(cookieUserId))
            {
                if(searchedSession.getDbfSessionUserId().equals(Long.parseLong(cookieUserId)))
                {
                    UserModel searchedUser = userService.readUserById(Long.parseLong(cookieUserId));

                    setSessionAttribute(httpServletRequest, ESessionAttributes.CSRF_TOKEN,
                            generateRandomString(32));
                    setSessionAttribute(httpServletRequest, ESessionAttributes.USER_ID,
                            searchedUser.getDbfUserId().toString());
                    setSessionAttribute(httpServletRequest, ESessionAttributes.USER_FIRST_NAME,
                            searchedUser.getDbfUserFirstName());
                    setSessionAttribute(httpServletRequest, ESessionAttributes.USER_LAST_NAME,
                            searchedUser.getDbfUserLastName());
                    setSessionAttribute(httpServletRequest, ESessionAttributes.USER_EMAIL,
                            searchedUser.getDbfUserEmail());
                    setSessionAttribute(httpServletRequest, ESessionAttributes.USER_ENCRYPTION_KEY,
                            getCookie(httpServletRequest, ECookieAttributes.USER_ENCRYPTION_KEY));
                    setSessionAttribute(httpServletRequest, ESessionAttributes.USER_STATUS,
                            searchedUser.getDbfUserStatus().name());
                }
            }
        }
    }

    public static void buildSession(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse,
                                    ISessionService sessionService,
                                    UserModel userModel)
    {
        String sessionToken = generateRandomString(64);

        SessionModel sessionModel = SessionModel
                .builder()
                .dbfSessionUserId(userModel.getDbfUserId())
                .dbfSessionToken(sessionToken)
                .dbfSessionExpiration(initializeExpirationDate())
                .build();

        sessionService.createSession(sessionModel);

        String decryptedEncryptionKey =
                decryptData(userModel.getDbfUserEncryptionKey(), userModel.getDbfUserPassword());

        setCookie(httpServletResponse, ECookieAttributes.USER_ID,
                userModel.getDbfUserId().toString());
        setCookie(httpServletResponse, ECookieAttributes.USER_ENCRYPTION_KEY,
                decryptedEncryptionKey);
        setCookie(httpServletResponse, ECookieAttributes.SESSION_TOKEN,
                sessionToken);
        setSessionAttribute(httpServletRequest, ESessionAttributes.USER_ID,
                userModel.getDbfUserId().toString());
        setSessionAttribute(httpServletRequest, ESessionAttributes.USER_FIRST_NAME,
                userModel.getDbfUserFirstName());
        setSessionAttribute(httpServletRequest, ESessionAttributes.USER_LAST_NAME,
                userModel.getDbfUserLastName());
        setSessionAttribute(httpServletRequest, ESessionAttributes.USER_EMAIL,
                userModel.getDbfUserEmail());
        setSessionAttribute(httpServletRequest, ESessionAttributes.USER_ENCRYPTION_KEY,
                decryptedEncryptionKey);
        setSessionAttribute(httpServletRequest, ESessionAttributes.USER_STATUS,
                userModel.getDbfUserStatus().name());
    }

    public static void invalidateSession(HttpServletRequest httpServletRequest,
                                         HttpServletResponse httpServletResponse)
    {
        setCookie(httpServletResponse, ECookieAttributes.USER_ID, "EMPTY");
        setCookie(httpServletResponse, ECookieAttributes.USER_ENCRYPTION_KEY, "EMPTY");
        setCookie(httpServletResponse, ECookieAttributes.SESSION_TOKEN, "EMPTY");
        setSessionAttribute(httpServletRequest, ESessionAttributes.USER_ID, "EMPTY");
        setSessionAttribute(httpServletRequest, ESessionAttributes.USER_FIRST_NAME, "EMPTY");
        setSessionAttribute(httpServletRequest, ESessionAttributes.USER_LAST_NAME, "EMPTY");
        setSessionAttribute(httpServletRequest, ESessionAttributes.USER_EMAIL, "EMPTY");
        setSessionAttribute(httpServletRequest, ESessionAttributes.USER_ENCRYPTION_KEY, "EMPTY");
        setSessionAttribute(httpServletRequest, ESessionAttributes.USER_STATUS, EUserStatus.GUEST.name());
    }

    public static String getSessionAttribute(HttpServletRequest httpServletRequest,
                                             ESessionAttributes sessionAttribute)
    {
        try
        {
            return httpServletRequest.getSession().getAttribute(sessionAttribute.name()).toString();
        }
        catch(Exception exception)
        {
            return "EMPTY";
        }
    }

    public static void setSessionAttribute(HttpServletRequest httpServletRequest,
                                           ESessionAttributes sessionAttribute,
                                           String sessionAttributeValue)
    {
        httpServletRequest.getSession().setAttribute(sessionAttribute.name(), sessionAttributeValue);
    }

    public static List<AccountModel> getDecryptedAccounts(HttpServletRequest httpServletRequest,
                                                          IAccountService accountService)
    {
        try
        {
            if(httpServletRequest.getSession().getAttribute
                    (ESessionAttributes.DECRYPTED_ACCOUNTS.name()) == null)
            {
                Long userId = Long.parseLong(getSessionAttribute(httpServletRequest,
                        ESessionAttributes.USER_ID));

                List<AccountModel> decryptedAccounts = accountService.readAllAccountsByUserId
                        (userId, httpServletRequest);

                setDecryptedAccounts(httpServletRequest, decryptedAccounts);
            }

            //noinspection unchecked
            return (List<AccountModel>)
                    httpServletRequest.getSession().getAttribute(ESessionAttributes.DECRYPTED_ACCOUNTS.name());
        }
        catch(Exception exception)
        {
            return List.of();
        }
    }

    public static void setDecryptedAccounts(HttpServletRequest httpServletRequest,
                                            List<AccountModel> decryptedAccounts)
    {
        httpServletRequest.getSession().setAttribute
                (ESessionAttributes.DECRYPTED_ACCOUNTS.name(), decryptedAccounts);
    }

    public static List<NoteModel> getDecryptedNotes(HttpServletRequest httpServletRequest,
                                                    INoteService noteService)
    {
        try
        {
            if(httpServletRequest.getSession().getAttribute
                    (ESessionAttributes.DECRYPTED_NOTES.name()) == null)
            {
                Long userId = Long.parseLong(getSessionAttribute(httpServletRequest,
                        ESessionAttributes.USER_ID));

                List<NoteModel> decryptedNotes = noteService.readAllNotesByUserId(userId, httpServletRequest);

                setDecryptedNotes(httpServletRequest, decryptedNotes);
            }

            //noinspection unchecked
            return (List<NoteModel>)
                    httpServletRequest.getSession().getAttribute(ESessionAttributes.DECRYPTED_NOTES.name());
        }
        catch(Exception exception)
        {
            return List.of();
        }
    }

    public static void setDecryptedNotes(HttpServletRequest httpServletRequest,
                                         List<NoteModel> decryptedNotes)
    {
        httpServletRequest.getSession().setAttribute
                (ESessionAttributes.DECRYPTED_NOTES.name(), decryptedNotes);
    }

    public static boolean isUserGuest(HttpServletRequest httpServletRequest)
    {
        String sessionUserStatus = getSessionAttribute(httpServletRequest, ESessionAttributes.USER_STATUS);
        return sessionUserStatus.equals(EUserStatus.GUEST.name());
    }

    public static boolean isCSRFTokenValid(HttpServletRequest httpServletRequest, String CSRFToken)
    {
        String sessionCSRFToken = getSessionAttribute(httpServletRequest, ESessionAttributes.CSRF_TOKEN);
        return CSRFToken.equals(sessionCSRFToken);
    }
}
