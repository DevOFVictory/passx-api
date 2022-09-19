package net.cuodex.passxapi.service;

import net.cuodex.passxapi.entity.LoginCredential;
import net.cuodex.passxapi.entity.UserAccount;
import net.cuodex.passxapi.repository.CredentialsRepository;
import net.cuodex.passxapi.repository.UserAccountRepository;
import net.cuodex.passxapi.returnables.DefaultReturnable;
import net.cuodex.passxapi.utils.OtherUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class StorageService {


    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private CredentialsRepository credentialsRepository;

    public DefaultReturnable getEntries(String sessionId, String ipAddress) {
        UserAccount user = authenticationService.getUser(sessionId, ipAddress);

        if (user == null)
            return new DefaultReturnable(HttpStatus.UNAUTHORIZED, "Session id is invalid or expired.");

        if (!authenticationService.getSession(sessionId).isActivated())
            return new DefaultReturnable(HttpStatus.FORBIDDEN, "Your session is not activated yet. Confirm your id using 2FA.");

        Set<LoginCredential> loginCredentials = user.getLoginCredentials();

        if (user.getServerSideEncryption())
            // Decrypt all credentials
            loginCredentials.forEach(loginCredential -> {
                loginCredentials.remove(loginCredential);
                loginCredentials.add(OtherUtils.decryptCredential(loginCredential));
            });

        return new DefaultReturnable(HttpStatus.OK, "Account entries retrieved successfully.").addData("amount", loginCredentials.size()).addData("entries", loginCredentials);
    }


    public DefaultReturnable getEntryById(String sessionId, String id, String ipAddress) {
        UserAccount user = authenticationService.getUser(sessionId, ipAddress);

        if (user == null)
            return new DefaultReturnable(HttpStatus.UNAUTHORIZED, "Session id is invalid or expired.");

        if (!authenticationService.getSession(sessionId).isActivated())
            return new DefaultReturnable(HttpStatus.FORBIDDEN, "Your session is not activated. Confirm your id using 2FA.");

        Set<LoginCredential> loginCredentials = user.getLoginCredentials();

        for (LoginCredential loginCredential : loginCredentials) {
            if (loginCredential.getId().toString().equals(id)) {
                    return new DefaultReturnable(HttpStatus.OK, "Account entry with id " + id + " successfully retrieved.")
                            .addData("entry", user.getServerSideEncryption() ? OtherUtils.decryptCredential(loginCredential) : loginCredential);
            }
        }
        return new DefaultReturnable(HttpStatus.NOT_FOUND, "Account entry with id " + id + " was not found on this account.");
    }

    public DefaultReturnable addEntry(String sessionId, String serviceName, String url, String description, String email, String username, String password, String ipAddress) {
        UserAccount user = authenticationService.getUser(sessionId, ipAddress);

        if (user == null)
            return new DefaultReturnable(HttpStatus.UNAUTHORIZED, "Session id is invalid or expired.");

        if (!authenticationService.getSession(sessionId).isActivated())
            return new DefaultReturnable(HttpStatus.FORBIDDEN, "Your session is not activated. Confirm your id using 2FA.");

        if (serviceName == null || url == null || description == null || email == null || username == null || password == null) {
            return new DefaultReturnable(HttpStatus.BAD_REQUEST, "Not all parameters present. (Read docs)");
        }

        LoginCredential credential = new LoginCredential();
        credential.setTitle(serviceName);
        credential.setUrl(url);
        credential.setDescription(description);
        credential.setEmail(email);
        credential.setUsername(username);
        credential.setPassword(password);

        credential.setId(credentialsRepository.getNextVal());
        user.addCredential(user.getServerSideEncryption() ? OtherUtils.encryptCredential(credential) : credential);
        userAccountRepository.save(user);

        return new DefaultReturnable(HttpStatus.CREATED, "Entry was successfully created.").addData("entry", credential);

    }

    public DefaultReturnable deleteEntry (String sessionId, String id, String ipAddress) {
        UserAccount user = authenticationService.getUser(sessionId, ipAddress);

        if (user == null)
            return new DefaultReturnable(HttpStatus.UNAUTHORIZED, "Session id is invalid or expired.");

        if (!authenticationService.getSession(sessionId).isActivated())
            return new DefaultReturnable(HttpStatus.FORBIDDEN, "Your session is not activated. Confirm your id using 2FA.");

        Set<LoginCredential> loginCredentials = user.getLoginCredentials();

        for (LoginCredential loginCredential : loginCredentials) {
            if (loginCredential.getId().toString().equals(id)) {

//                credentialsRepository.deleteLoginCredentialById(Long.valueOf(id));
                loginCredentials.remove(loginCredential);
                userAccountRepository.save(user);

                return new DefaultReturnable(HttpStatus.OK, "Entry with id " + id + " successfully deleted.");
            }
        }
        return new DefaultReturnable(HttpStatus.NOT_FOUND, "Entry with id " + id + " was not found on this account.");
    }

    public DefaultReturnable updateEntry(String sessionId, String id, String entryService, String entryUrl, String entryDescription, String entryEmail, String entryUsername, String entryPassword, String ipAddress) {
        UserAccount user = authenticationService.getUser(sessionId, ipAddress);

        if (user == null)
            return new DefaultReturnable(HttpStatus.UNAUTHORIZED, "Session id is invalid or expired.");

        if (!authenticationService.getSession(sessionId).isActivated())
            return new DefaultReturnable(HttpStatus.FORBIDDEN, "Your session is not activated. Confirm your id using 2FA.");

        Set<LoginCredential> loginCredentials = user.getLoginCredentials();

        for (LoginCredential loginCredential : loginCredentials) {
            if (loginCredential.getId().toString().equals(id)) {
                loginCredential.setTitle(entryService);
                loginCredential.setUrl(entryUrl);
                loginCredential.setDescription(entryDescription);
                loginCredential.setEmail(entryEmail);
                loginCredential.setUsername(entryUsername);
                loginCredential.setPassword(entryPassword);

                credentialsRepository.saveAndFlush(user.getServerSideEncryption() ? OtherUtils.encryptCredential(loginCredential) : loginCredential);
                userAccountRepository.saveAndFlush(user);

                return new DefaultReturnable(HttpStatus.OK, "Entry with id " + id + " successfully updated.")
                        .addData("entry", user.getServerSideEncryption() ? OtherUtils.decryptCredential(loginCredential) : loginCredential);
            }
        }
        return new DefaultReturnable(HttpStatus.NOT_FOUND, "Entry with id " + id + " was not found on this account.");
    }
}
