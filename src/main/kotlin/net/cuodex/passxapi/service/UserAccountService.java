package net.cuodex.passxapi.service;

import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.recovery.RecoveryCodeGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import net.cuodex.passxapi.PassxApiApplication;
import net.cuodex.passxapi.entity.UserAccount;
import net.cuodex.passxapi.repository.UserAccountRepository;
import net.cuodex.passxapi.returnables.DefaultReturnable;
import net.cuodex.passxapi.utils.OtherUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class UserAccountService {

    @Autowired
    private SecretGenerator secretGenerator;


    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private StorageService storageService;

    @Autowired
    private CodeVerifier verifier;

    @Autowired
    private RecoveryCodeGenerator recoveryCodeGenerator;

    @Autowired
    private UserAccountRepository userRepository;

    public DefaultReturnable getInformation(String sessionId, String ipAddress) {
        UserAccount user = authenticationService.getUser(sessionId, ipAddress);
        if (user == null)
            return new DefaultReturnable(HttpStatus.UNAUTHORIZED, "Session id is invalid or expired.");

        if (!authenticationService.getSession(sessionId).isActivated())
            return new DefaultReturnable(HttpStatus.FORBIDDEN, "Your session is not activated. Confirm your id using 2FA.");

        return new DefaultReturnable("Successfully retrieved user information.").addData("user", user);
    }

    public DefaultReturnable updateInformation(String sessionId, String passwordTest, Map<String, String> data, String ipAddress) {

        UserAccount user = authenticationService.getUser(sessionId, ipAddress);
        if (user == null)
            return new DefaultReturnable(HttpStatus.UNAUTHORIZED, "Session id is invalid or expired.");

        if (!authenticationService.getSession(sessionId).isActivated())
            return new DefaultReturnable(HttpStatus.FORBIDDEN, "Your session is not activated. Confirm your id using 2FA.");

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

    public DefaultReturnable changePassword(String sessionId, String passwordTest, String newPasswordTest, List<Map<String, String>> entries, String ipAddress) {
        UserAccount user = authenticationService.getUser(sessionId, ipAddress);
        if (user == null)
            return new DefaultReturnable(HttpStatus.UNAUTHORIZED, "Session id is invalid or expired.");

        if (!authenticationService.getSession(sessionId).isActivated())
            return new DefaultReturnable(HttpStatus.FORBIDDEN, "Your session is not activated. Confirm your id using 2FA.");

        if (!user.getPasswordTest().equals(passwordTest))
            return new DefaultReturnable(HttpStatus.FORBIDDEN, "Invalid password test for corresponding user session. ('" + user.getUsername() + "')");

        if (newPasswordTest.equals(passwordTest)) {
            return new DefaultReturnable(HttpStatus.NOT_MODIFIED, "Your new password must not be equal to your old password");
        }

        boolean error = false;
        for (Map<String, String> stringEntry : entries) {
            error = storageService.updateEntry(sessionId, stringEntry.get("id"), stringEntry.get("title"), stringEntry.get("url"),
                    stringEntry.get("description"), stringEntry.get("username"), stringEntry.get("email"), stringEntry.get("password"), ipAddress).getResponseEntity().getStatusCodeValue() != 200;
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

    public DefaultReturnable deleteUser(String sessionId, String ipAddress) {
        UserAccount user = authenticationService.getUser(sessionId, ipAddress);

        if (user == null)
            return new DefaultReturnable(HttpStatus.UNAUTHORIZED, "Session id is invalid or expired.");

        if (!authenticationService.getSession(sessionId).isActivated())
            return new DefaultReturnable(HttpStatus.FORBIDDEN, "Your session is not activated. Confirm your id using 2FA.");

        authenticationService.invalidateSession(sessionId);
        userRepository.delete(user);

        System.gc();

        return new DefaultReturnable("Account was successfully deleted.");
    }

    public DefaultReturnable enable2Fa(String sessionId, String prefix, String ipAddress) {
        UserAccount user = authenticationService.getUser(sessionId, ipAddress);

        if (user == null)
            return new DefaultReturnable(HttpStatus.UNAUTHORIZED, "Session id is invalid or expired.");

        if (user.isTwoFactorEnabled())
            return new DefaultReturnable(HttpStatus.CONFLICT, "2FA is already activated for this account.");

        String secret = secretGenerator.generate();
        user.setTotpSecret(secret);
        userRepository.save(user);
        return new DefaultReturnable(HttpStatus.CREATED, "2FA secret successfully generated. Please finish the process by confirming your identity.")
                .addData("secret", secret)
                .addData("qrCode", prefix + secret);
    }

    public DefaultReturnable confirm2Fa(String sessionId, String otp, boolean rememberMe, String ipAddress) {
        UserAccount user = authenticationService.getUser(sessionId, ipAddress);

        if (user == null)
            return new DefaultReturnable(HttpStatus.UNAUTHORIZED, "Session id is invalid or expired.");

        if (user.getTotpSecret() == null || user.getTotpSecret().isEmpty())
            return new DefaultReturnable(HttpStatus.CONFLICT, "2FA is not enabled for this account.");

        if (user.isTwoFactorEnabled())
            return new DefaultReturnable(HttpStatus.CONFLICT, "2FA is already activated for this account.");

        if (!verifier.isValidCode(user.getTotpSecret(), otp))
            return new DefaultReturnable(HttpStatus.UNAUTHORIZED, "2FA OTP-Code is invalid.");

        user.setTwoFactorEnabled(true);
        user.setRememberMe(rememberMe);
        userRepository.save(user);
        return new DefaultReturnable(HttpStatus.OK, "2FA was fully enabled and is now active.");
    }

    public DefaultReturnable getBackupCodes(String sessionId, String ipAddress) {
        UserAccount user = authenticationService.getUser(sessionId, ipAddress);

        if (user == null)
            return new DefaultReturnable(HttpStatus.UNAUTHORIZED, "Session id is invalid or expired.");

        if (!authenticationService.getSession(sessionId).isActivated())
            return new DefaultReturnable(HttpStatus.FORBIDDEN, "Your session is not activated. Confirm your id using 2FA.");

        if (!user.isTwoFactorEnabled())
            return new DefaultReturnable(HttpStatus.CONFLICT, "2FA is not enabled for this account.");

        String[] codes = recoveryCodeGenerator.generateCodes(8);
        return new DefaultReturnable(HttpStatus.OK, "Here are your 8 recovery codes.")
                .addData("codes", Arrays.asList(codes));

    }

    public DefaultReturnable disable2Fa(String sessionId, String clientIp) {
        UserAccount user = authenticationService.getUser(sessionId, clientIp);

        if (user == null)
            return new DefaultReturnable(HttpStatus.UNAUTHORIZED, "Session id is invalid or expired.");

        if (!user.isTwoFactorEnabled() && ((user.getTotpSecret() == null || user.getTotpSecret().isEmpty())))
            return new DefaultReturnable(HttpStatus.CONFLICT, "2FA is not activated or enabled for this account.");

        if (!authenticationService.getSession(sessionId).isActivated())
            return new DefaultReturnable(HttpStatus.FORBIDDEN, "Your session is not activated. Confirm your id using 2FA.");

        user.setTotpSecret(null);
        user.setTwoFactorEnabled(false);
        userRepository.save(user);
        return new DefaultReturnable(HttpStatus.OK, "2FA was successfully disabled.");
    }
}
