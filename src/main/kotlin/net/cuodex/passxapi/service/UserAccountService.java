package net.cuodex.passxapi.service;

import net.cuodex.passxapi.entity.UserAccount;
import net.cuodex.passxapi.repository.UserAccountRepository;
import net.cuodex.passxapi.returnables.DefaultReturnable;
import net.cuodex.passxapi.utils.OtherUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class UserAccountService {

    @Autowired
    private AuthenticationService authenticationService;

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
            return new DefaultReturnable(HttpStatus.FORBIDDEN, "Invalid password test for corresponding user session ('"+user.getUsername()+"')");

        String username = data.get("username");

        boolean usernameChanged = false;
        boolean emailChanged = false;

        if (username != null) {
            if (!OtherUtils.isUsernameValid(username))
                return new DefaultReturnable(HttpStatus.BAD_REQUEST, "Invalid username. (See docs)");

            if (userRepository.existsByUsername(username)){
                return new DefaultReturnable(HttpStatus.BAD_REQUEST, "Username already taken.");
            }

            usernameChanged = true;

        }

        String email = data.get("email");

        if (email != null) {
            if (!OtherUtils.isEmailValid(email))
                return new DefaultReturnable(HttpStatus.BAD_REQUEST, "Invalid email address. (See docs)");


            emailChanged = true;
        }

        if (!usernameChanged && !emailChanged)
            return new DefaultReturnable(HttpStatus.NOT_MODIFIED, "No information were given.").addData("user", user);

        if (usernameChanged)
            user.setUsername(username);

        if (emailChanged)
            user.setEmail(email);

        userRepository.save(user);
        return new DefaultReturnable("Successfully updated user information.").addData("user", user);

    }

}
