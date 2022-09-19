package net.cuodex.passxapi.service;

import net.cuodex.passxapi.PassxApiApplication;
import net.cuodex.passxapi.entity.UserAccount;
import net.cuodex.passxapi.repository.UserAccountRepository;
import net.cuodex.passxapi.returnables.DefaultReturnable;
import net.cuodex.passxapi.utils.OtherUtils;
import net.cuodex.passxapi.utils.PassxUserSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AuthenticationService {

    private final List<PassxUserSession> activeSessions;

    @Autowired
    private UserAccountRepository userRepository;

    public AuthenticationService() {
        this.activeSessions = new ArrayList<>();
    }

    public PassxUserSession authenticate(String username, String password, String ipAddress) {

        if (!userRepository.existsByUsername(username))
            return null;


        UserAccount userAccount = userRepository.findByUsername(username).get();

        userAccount.setLastSeen(OtherUtils.getTimestamp());
        userAccount.setIpAddress(ipAddress);
        userRepository.save(userAccount);
//
        String encryptionTest = userAccount.getPasswordTest();

        if (!password.equals(encryptionTest)) {
            return null;
        }

        // Generate session which is not in use
        PassxUserSession session;
        List<String> activeSessionIds = OtherUtils.getSessionIdList(activeSessions);
        do {
            session = new PassxUserSession(userAccount.getId(), ipAddress);
        }while (activeSessionIds.contains(session.getSessionId()));

        // Deactivate old sessions
        activeSessions.removeIf(activeSession -> activeSession.getAccountId() == userAccount.getId());

        // Add new session
        this.activeSessions.add(session);

        PassxApiApplication.LOGGER.info("User '" + username + "' successfully authenticated. ("+session.getSessionId()+")");
        return session;

    }

    public boolean isSessionValid(String sessionId, String ipAddress) {
        if (OtherUtils.getSessionIdList(activeSessions).contains(sessionId)) {
            if (System.currentTimeMillis() - getSession(sessionId).getCreatedAt() > 10000L) {
                invalidateSession(sessionId);
                return false;
            }

            return getSession(sessionId).getIpAddress().equals(ipAddress);
        }
        return false;
    }

    public void invalidateSession(String sessionId) {
        activeSessions.removeIf(activeSession -> activeSession.getSessionId().equals(sessionId));
    }

    public UserAccount getUser(String sessionId, String ipAddress) {
        //        if (userAccount != null) {
//            userAccount.setLastSeen(OtherUtils.getTimestamp());
//            userRepository.save(userAccount);
//        }

//        return userRepository.findById(1L).get();
        if (isSessionValid(sessionId, ipAddress)) {
            return userRepository.getById(getSession(sessionId).getAccountId());
        }else {
            return null;
        }
    }

    public String getSessionId(UserAccount userAccount) {
        for (PassxUserSession session : activeSessions) {
            if (session.getAccountId() == userAccount.getId()) {
                return session.getSessionId();
            }
        }
        return null;
    }

    public DefaultReturnable createUser(final String username, final String email, final String passwordTest, final boolean serverSideEncryption, String ipAddress) {

        if (!(username.matches("^[a-zA-Z0-9_]*$") && username.length() >= 3 && username.length() <= 16))
            return new DefaultReturnable(HttpStatus.BAD_REQUEST, "Invalid username.");


        if (!OtherUtils.isEmailValid(email))
            return new DefaultReturnable(HttpStatus.BAD_REQUEST, "Invalid email address.");


        // add check for username exists in a DB
        if (userRepository.existsByUsername(username))
            return new DefaultReturnable(HttpStatus.BAD_REQUEST, "Username already taken.");


        // create user object
        UserAccount user = new UserAccount();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordTest(passwordTest);
        user.setCreatedAt(OtherUtils.getTimestamp());
        user.setLastSeen(OtherUtils.getTimestamp());
        user.setServerSideEncryption(serverSideEncryption);
        user.setIpAddress(ipAddress);

        userRepository.save(user);
        PassxApiApplication.LOGGER.info("User '" + username + "' successfully created.");

        return new DefaultReturnable(HttpStatus.CREATED, "User successfully created.").addData("user", user);
    }

    public DefaultReturnable checkSession(String sessionId, String ipAddress) {
        if (!isSessionValid(sessionId, ipAddress))
            return new DefaultReturnable(HttpStatus.UNAUTHORIZED, "Session id is invalid or expired.");

        PassxUserSession session = getSession(sessionId);

        if (!session.isActivated())
            return new DefaultReturnable(HttpStatus.ACCEPTED, "Session id is valid but not activated yet.")
                    .addData("session", session);

        return new DefaultReturnable(HttpStatus.OK, "Session id is valid.")
                .addData("session", session);
    }

    public PassxUserSession getSession(String sessionId) {
        return activeSessions.stream().filter(activeSession -> activeSession.getSessionId().equals(sessionId)).toList().get(0);
    }
}
