package net.cuodex.passxapi.controller;

import net.cuodex.passxapi.dto.LoginDto;
import net.cuodex.passxapi.dto.RegisterDto;
import net.cuodex.passxapi.dto.SessionDto;
import net.cuodex.passxapi.entity.UserAccount;
import net.cuodex.passxapi.returnables.DefaultReturnable;
import net.cuodex.passxapi.service.AuthenticationService;
import net.cuodex.passxapi.service.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationService authenticationService;


    @PostMapping("/login")
    public DefaultReturnable authenticateUser(@RequestBody LoginDto loginDto){
        String sessionId = authenticationService.authenticate(
                loginDto.getUsername(), loginDto.getPasswordTest());

        if (sessionId.startsWith("ERROR: "))
            return new DefaultReturnable(HttpStatus.UNAUTHORIZED, sessionId.replace("ERROR: ", ""));

        DefaultReturnable returnable = new DefaultReturnable(HttpStatus.OK, "Successfully logged in.");
        returnable.addData("sessionId", sessionId);

        return returnable;
    }

    @PostMapping("/register")
    public DefaultReturnable registerUser(@RequestBody RegisterDto signUpDto){

        return authenticationService.createUser(signUpDto.getUsername(), signUpDto.getEmail(), signUpDto.getPasswordTest());

    }

    @PostMapping("/logout")
    public DefaultReturnable logoutUser(@RequestBody SessionDto sessionDto){
        UserAccount user = authenticationService.getUser(sessionDto.getSessionId());
        if (user == null)
            return new DefaultReturnable(HttpStatus.UNAUTHORIZED, "Session id is invalid or expired.");

        authenticationService.invalidateSession(sessionDto.getSessionId());
        return new DefaultReturnable("Successfully logged out.");
    }

    @PostMapping("/check-session")
    public DefaultReturnable checkSession(SessionDto sessionDto) {
        return authenticationService.checkSession(sessionDto.getSessionId());
    }


}
