package net.cuodex.passxapi.service;

import net.cuodex.passxapi.entity.UserAccount;
import net.cuodex.passxapi.repository.UserAccountRepository;
import net.cuodex.passxapi.returnables.DefaultReturnable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class StorageService {


    @Autowired
    private AuthenticationService authenticationService;

    public DefaultReturnable getEntries(String sessionId) {
        UserAccount user = authenticationService.getUser(sessionId);

        if (user == null)
            return new DefaultReturnable(HttpStatus.FORBIDDEN, "Session id is invalid or expired.");

        return new DefaultReturnable(HttpStatus.OK, "Account entries retrieved successfully.").addData("entries", user.getLoginCredentials());
    }
}
