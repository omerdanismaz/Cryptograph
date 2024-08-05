package dev.omerdanismaz.Cryptograph.services;

import dev.omerdanismaz.Cryptograph.iservices.ISessionService;
import dev.omerdanismaz.Cryptograph.models.SessionModel;
import dev.omerdanismaz.Cryptograph.repositories.SessionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static dev.omerdanismaz.Cryptograph.functions.GeneralOperations.initializeExpirationDate;

@Service
@AllArgsConstructor
public class SessionServiceImpl implements ISessionService
{
    private final SessionRepository sessionRepository;

    @Override
    public void createSession(SessionModel sessionModel)
    {
        sessionModel.setDbfSessionExpiration(initializeExpirationDate());
        sessionModel.setDbfSessionCreatedOn(new Date());
        sessionRepository.save(sessionModel);
    }

    @Override
    public SessionModel readSessionByToken(String sessionToken)
    {
        Optional<SessionModel> searchedSession = sessionRepository.findByDbfSessionToken(sessionToken);

        return searchedSession.orElseGet(() -> SessionModel.builder()
                .dbfSessionId(0L)
                .dbfSessionUserId(0L)
                .dbfSessionToken("EMPTY")
                .dbfSessionExpiration(new Date())
                .dbfSessionCreatedOn(new Date())
                .build());
    }

    @Override
    public void deleteSession(SessionModel sessionModel)
    {
        sessionRepository.delete(sessionModel);
    }

    @Override
    public void deleteAllUserSessions(Long sessionUserId)
    {
        List<SessionModel> userSessions = sessionRepository.findByDbfSessionUserId(sessionUserId);
        sessionRepository.deleteAll(userSessions);
    }

    @Override
    public boolean isSessionExpired(SessionModel sessionModel)
    {
        return sessionModel.getDbfSessionExpiration().before(new Date());
    }
}
