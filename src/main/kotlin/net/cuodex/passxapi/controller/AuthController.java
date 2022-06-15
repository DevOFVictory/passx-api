package net.cuodex.passxapi.controller;

import net.cuodex.passxapi.dto.LoginDto;
import net.cuodex.passxapi.dto.RegisterDto;
import net.cuodex.passxapi.entity.LoginCredential;
import net.cuodex.passxapi.entity.UserAccount;
import net.cuodex.passxapi.repository.UserAccountRepository;
import net.cuodex.passxapi.returnables.DefaultReturnable;
import net.cuodex.passxapi.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationService authenticationService;


    @PostMapping("/login")
    public ResponseEntity<DefaultReturnable> authenticateUser(@Valid @RequestBody LoginDto loginDto){
        String sessionId = authenticationService.authenticate(
                loginDto.getUsername(), loginDto.getPasswordTest());

        if (sessionId.startsWith("ERROR: "))
            return new DefaultReturnable(HttpStatus.UNAUTHORIZED, sessionId.replace("ERROR: ", "")).getResponseEntity();

        DefaultReturnable returnable = new DefaultReturnable(HttpStatus.OK, "Successfully logged in.");
        UserAccount user = authenticationService.getUser(sessionId);
        returnable.addData("sessionId", sessionId);
        returnable.addData("username", user.getUsername());
        returnable.addData("email", user.getEmail());

        return returnable.getResponseEntity();
    }

    @PostMapping("/register")
    public ResponseEntity<DefaultReturnable> registerUser(@Valid @RequestBody RegisterDto signUpDto){
        return authenticationService.createUser(signUpDto.getUsername(), signUpDto.getEmail().toLowerCase(), signUpDto.getPasswordTest()).getResponseEntity();

    }

    @PostMapping("/logout")
    public ResponseEntity<DefaultReturnable> logoutUser(@RequestHeader(value = "Authorization") String sessionId){
        sessionId = sessionId.split(" ")[sessionId.split(" ").length - 1];
        UserAccount user = authenticationService.getUser(sessionId);
        if (user == null)
            return new DefaultReturnable(HttpStatus.UNAUTHORIZED, "Session id is invalid or expired.").getResponseEntity();

        authenticationService.invalidateSession(sessionId);
        return new DefaultReturnable("Successfully logged out.").getResponseEntity();
    }

    @PostMapping("/check-session")
    public ResponseEntity<DefaultReturnable> checkSession(@RequestHeader(value = "Authorization") String sessionId) {
        sessionId = sessionId.split(" ")[sessionId.split(" ").length - 1];
        return authenticationService.checkSession(sessionId).getResponseEntity();
    }


}
