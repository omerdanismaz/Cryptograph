package dev.omerdanismaz.Cryptograph.iservices;

import dev.omerdanismaz.Cryptograph.models.SessionModel;

public interface ISessionService
{
    void createSession(SessionModel sessionModel);
    SessionModel readSessionByToken(String sessionToken);
    void deleteSession(SessionModel sessionModel);
    void deleteAllUserSessions(Long sessionUserId);
    boolean isSessionExpired(SessionModel sessionModel);
}
