package net.cuodex.passxapi.service;

import net.cuodex.passxapi.PassxApiApplication;
import net.cuodex.passxapi.entity.UserAccount;
import net.cuodex.passxapi.repository.UserAccountRepository;
import net.cuodex.passxapi.returnables.DefaultReturnable;
import net.cuodex.passxapi.utils.OtherUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class AuthenticationService {

    private final Map<String, UserAccount> activeSessions;

    @Autowired
    private UserAccountRepository userRepository;

    public AuthenticationService() {
        this.activeSessions = new HashMap<>();
    }

    public String authenticate(String username, String password) {

        if (!userRepository.existsByUsername(username))
            return "ERROR: Username does not exists.";


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

    public DefaultReturnable createUser(final String username, final String email, final String passwordTest) {

        if (!(username.matches("^[a-zA-Z0-9_]*$") && username.length() >= 3 && username.length() <= 16))
            return new DefaultReturnable(HttpStatus.BAD_REQUEST, "Invalid username.");


        if (!OtherUtils.isEmailValid(email))
            return new DefaultReturnable(HttpStatus.BAD_REQUEST, "Invalid email address.");


        if (!passwordTest.matches("^[a-zA-Z0-9=]*$"))
            return new DefaultReturnable(HttpStatus.BAD_REQUEST, "Invalid password test.");

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

        userRepository.save(user);

        return new DefaultReturnable("User successfully created.").addData("user", user);
    }

    public DefaultReturnable deleteUser(String sessionId, String passwordTest) {
        UserAccount user = this.getUser(sessionId);

        if (user == null)
            return new DefaultReturnable(HttpStatus.FORBIDDEN, "Session id is invalid or expired.");

        if (!user.getPasswordTest().equals(passwordTest))
            return new DefaultReturnable(HttpStatus.FORBIDDEN, "Invalid password test for corresponding user session. ('"+user.getUsername()+"')");

        this.invalidateSession(sessionId);
        userRepository.delete(user);

        System.gc();

        return new DefaultReturnable("Account was successfully deleted.");
    }

    public DefaultReturnable checkSession(String sessionId) {
        if (isSessionValid(sessionId)) {
            return new DefaultReturnable(HttpStatus.OK, "Session id is valid.");
        }else {
            return new DefaultReturnable(HttpStatus.UNAUTHORIZED, "Session id is invalid or expired.");
        }
    }
}
