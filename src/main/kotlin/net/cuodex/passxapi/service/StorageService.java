package net.cuodex.passxapi.service;

import net.cuodex.passxapi.entity.LoginCredential;
import net.cuodex.passxapi.entity.UserAccount;
import net.cuodex.passxapi.repository.UserAccountRepository;
import net.cuodex.passxapi.returnables.DefaultReturnable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ldap.embedded.EmbeddedLdapProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class StorageService {


    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private UserAccountRepository userAccountRepository;

    public DefaultReturnable getEntries(String sessionId) {
        UserAccount user = authenticationService.getUser(sessionId);

        if (user == null)
            return new DefaultReturnable(HttpStatus.FORBIDDEN, "Session id is invalid or expired.");

        Set<LoginCredential> loginCredentials = user.getLoginCredentials();

        return new DefaultReturnable(HttpStatus.OK, "Account entries retrieved successfully.").addData("amount", loginCredentials.size()).addData("entries", loginCredentials);
    }


    public DefaultReturnable getEntryById(String sessionId, String id) {
        UserAccount user = authenticationService.getUser(sessionId);

        if (user == null)
            return new DefaultReturnable(HttpStatus.FORBIDDEN, "Session id is invalid or expired.");

        Set<LoginCredential> loginCredentials = user.getLoginCredentials();

        for (LoginCredential loginCredential : loginCredentials) {
            if (loginCredential.getId().toString().equals(id)) {
                return new DefaultReturnable(HttpStatus.OK, "Account entry with id " + id + " successfully retrieved.").addData("entry", loginCredential);
            }
        }
        return new DefaultReturnable(HttpStatus.NOT_FOUND, "Account entry with id " + id + " was not found on this account.");
    }

    public DefaultReturnable addEntry(String sessionId, String serviceName, String url, String description, String email, String username, String password) {
        UserAccount user = authenticationService.getUser(sessionId);

        if (user == null)
            return new DefaultReturnable(HttpStatus.FORBIDDEN, "Session id is invalid or expired.");

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

        user.addCredential(credential);

        userAccountRepository.save(user).getId();

        return new DefaultReturnable(HttpStatus.CREATED, "Entry was successfully created.").addData("entry", credential);

    }
}
