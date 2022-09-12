package net.cuodex.passxapi.service;

import net.cuodex.passxapi.PassxApiApplication;
import net.cuodex.passxapi.entity.LoginCredential;
import net.cuodex.passxapi.entity.UserAccount;
import net.cuodex.passxapi.repository.UserAccountRepository;
import net.cuodex.passxapi.returnables.DefaultReturnable;
import net.cuodex.passxapi.utils.OtherUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class UserAccountService {


    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private StorageService storageService;

    @Autowired
    private UserAccountRepository userRepository;

    public DefaultReturnable getInformation(String sessionId) {
        UserAccount user = authenticationService.getUser(sessionId);
        if (user == null)
            return new DefaultReturnable(HttpStatus.FORBIDDEN, "Session id is invalid or expired.");


        return new DefaultReturnable("Successfully retrieved user information.").addData("user", user);
    }

    public DefaultReturnable updateInformation(String sessionId, String passwordTest, Map<String, String> data) {

        UserAccount user = authenticationService.getUser(sessionId);
        if (user == null)
            return new DefaultReturnable(HttpStatus.FORBIDDEN, "Session id is invalid or expired.");

        if (!user.getPasswordTest().equals(passwordTest))
            return new DefaultReturnable(HttpStatus.FORBIDDEN, "Invalid password test for corresponding user session ('" + user.getUsername() + "')");


        boolean usernameChanged = data.containsKey("username");
        boolean emailChanged = data.containsKey("email");


        if (usernameChanged) {
            String username = data.get("username");
            if (!OtherUtils.isUsernameValid(username))
                return new DefaultReturnable(HttpStatus.BAD_REQUEST, "Invalid username. (See docs)");

            if (userRepository.existsByUsername(username)) {
                return new DefaultReturnable(HttpStatus.BAD_REQUEST, "Username already taken.");
            }

            user.setUsername(username);
        }

        if (emailChanged) {
            String email = data.get("email").toLowerCase();

            if (!OtherUtils.isEmailValid(email))
                return new DefaultReturnable(HttpStatus.BAD_REQUEST, "Invalid email address. (See docs)");

            user.setEmail(email);
        }


        userRepository.save(user);
        PassxApiApplication.LOGGER.info("User with id '" + user.getId() + "' successfully updated information.");
        return new DefaultReturnable("Successfully updated user information.").addData("user", user);

    }

    public DefaultReturnable changePassword(String sessionId, String passwordTest, String newPasswordTest, List<Map<String, String>> entries) {
        UserAccount user = authenticationService.getUser(sessionId);
        if (user == null)
            return new DefaultReturnable(HttpStatus.FORBIDDEN, "Session id is invalid or expired.");

        if (!user.getPasswordTest().equals(passwordTest))
            return new DefaultReturnable(HttpStatus.FORBIDDEN, "Invalid password test for corresponding user session. ('" + user.getUsername() + "')");

        if (newPasswordTest.equals(passwordTest)) {
            return new DefaultReturnable(HttpStatus.NOT_MODIFIED, "Your new password must not be equal to your old password");
        }

        boolean error = false;
        for (Map<String, String> stringEntry : entries) {
            error = storageService.updateEntry(sessionId, stringEntry.get("id"), stringEntry.get("title"), stringEntry.get("url"),
                    stringEntry.get("description"), stringEntry.get("username"), stringEntry.get("email"), stringEntry.get("password")).getResponseEntity().getStatusCodeValue() != 200;
        }

        if (error) {
            PassxApiApplication.LOGGER.warn(entries.toString());
            return new DefaultReturnable(HttpStatus.BAD_REQUEST, "Something went wrong with the entries data");
        }

        user.setPasswordTest(newPasswordTest);
        userRepository.save(user);

        PassxApiApplication.LOGGER.info("User '" + user.getUsername() + "' successfully changed master password.");
        return new DefaultReturnable("The master password was successfully changed and all entries were updated.");
    }

    public DefaultReturnable deleteUser(String sessionId) {
        UserAccount user = authenticationService.getUser(sessionId);

        if (user == null)
            return new DefaultReturnable(HttpStatus.FORBIDDEN, "Session id is invalid or expired.");

        authenticationService.invalidateSession(sessionId);
        userRepository.delete(user);

        System.gc();

        return new DefaultReturnable("Account was successfully deleted.");
    }
}
