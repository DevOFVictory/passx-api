package net.cuodex.passxapi.utils;

import net.cuodex.passxapi.PassxApiApplication;
import net.cuodex.passxapi.entity.UserAccount;
import net.cuodex.passxapi.repository.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class AuthenticationManager {

    private final Map<String, UserAccount> activeSessions;

    @Autowired
    private UserAccountRepository userRepository;

    public AuthenticationManager() {
        this.activeSessions = new HashMap<>();
    }

    public String authenticate(String username, String password) {

        if (!userRepository.existsByUsername(username))
            return "ERROR: Username does not exists.";

//        AESManager aesManager = new AESManager(password);

        UserAccount userAccount = userRepository.findByUsername(username).get();

        userAccount.setLastSeen(OtherUtils.getTimestamp());
        userRepository.save(userAccount);
//
        String encryptionTest = userAccount.getPasswordTest();
//        System.out.println(encryptionTest);
//
//        String decrypted = aesManager.decrypt(encryptionTest);

        if (!password.equals(encryptionTest))
            return "ERROR: Password is invalid.";

        String sessionId = UUID.randomUUID().toString();

        // deactivate old sessions
        for (Map.Entry<String, UserAccount> entry : this.activeSessions.entrySet()) {
            if (entry.getValue().equals(userAccount))
                this.activeSessions.remove(entry.getKey());
        }

        this.activeSessions.put(sessionId, userAccount);


        PassxApiApplication.LOGGER.info("User '" + username + "' successfully authenticated. ("+sessionId+")");
        return sessionId;

    }

    public boolean isSessionValid(String sessionId) {
        return this.activeSessions.containsKey(sessionId);
    }

    public void invalidateSession(String sessionId) {
        this.activeSessions.remove(sessionId);
    }

    public UserAccount getUser(String sessionId) {
        UserAccount userAccount = this.activeSessions.get(sessionId);
        if (userAccount != null) {
            userAccount.setLastSeen(OtherUtils.getTimestamp());
            userRepository.save(userAccount);
        }

//        return userRepository.findById(1L).get();
        return userAccount;
    }

    public String getSessionId(UserAccount userAccount) {
        for (Map.Entry<String, UserAccount> entry : this.activeSessions.entrySet()) {
            if (entry.getValue().equals(userAccount))
                return entry.getKey();
        }
        return null;
    }

    public void forceLogin(String sessionId, long id) {
        activeSessions.put(sessionId, userRepository.getById(id));
    }

}
