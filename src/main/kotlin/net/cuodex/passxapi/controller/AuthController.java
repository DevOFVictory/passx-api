package net.cuodex.passxapi.controller;

import net.cuodex.passxapi.dto.Confirm2FaCodeDto;
import net.cuodex.passxapi.dto.LoginDto;
import net.cuodex.passxapi.dto.RegisterDto;
import net.cuodex.passxapi.entity.UserAccount;
import net.cuodex.passxapi.returnables.DefaultReturnable;
import net.cuodex.passxapi.service.AuthenticationService;
import net.cuodex.passxapi.service.RequestService;
import net.cuodex.passxapi.utils.PassxUserSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@CrossOrigin
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private RequestService requestService;


    @PostMapping("/login")
    public ResponseEntity<DefaultReturnable> authenticateUser(@Valid @RequestBody LoginDto loginDto, HttpServletRequest request) {
        PassxUserSession session = authenticationService.authenticate(
                loginDto.getUsername(), loginDto.getPasswordTest(), requestService.getClientIp(request));

        if (session == null)
            return new DefaultReturnable(HttpStatus.UNAUTHORIZED, "Username or password is invalid").getResponseEntity();

        DefaultReturnable returnable = new DefaultReturnable(HttpStatus.OK, "Successfully logged in.");
        UserAccount user = authenticationService.getUser(session.getSessionId(), requestService.getClientIp(request));
        returnable.addData("sessionId", session.getSessionId());
        returnable.addData("username", user.getUsername());
        returnable.addData("email", user.getEmail());

        return returnable.getResponseEntity();
    }

    @PostMapping("/confirm-identity")
    public ResponseEntity<DefaultReturnable> confirmIdentity(@RequestHeader(value = "Authorization") String sessionId, @Valid @RequestBody Confirm2FaCodeDto confirm2FaCodeDto, HttpServletRequest request) {
        sessionId = sessionId.split(" ")[sessionId.split(" ").length - 1];
        return authenticationService.confirmIdentity(sessionId, confirm2FaCodeDto.getOtp(), confirm2FaCodeDto.isRememberMe(), requestService.getClientIp(request)).getResponseEntity();
    }


    @PostMapping("/register")
    public ResponseEntity<DefaultReturnable> registerUser(@Valid @RequestBody RegisterDto signUpDto, HttpServletRequest request){
        return authenticationService.createUser(signUpDto.getUsername(), signUpDto.getEmail().toLowerCase(), signUpDto.getPasswordTest(), signUpDto.getServerSideEncryption(), signUpDto.getHutchaToken(), requestService.getClientIp(request)).getResponseEntity();

    }

    @PostMapping("/logout")
    public ResponseEntity<DefaultReturnable> logoutUser(@RequestHeader(value = "Authorization") String sessionId, HttpServletRequest request){
        sessionId = sessionId.split(" ")[sessionId.split(" ").length - 1];
        UserAccount user = authenticationService.getUser(sessionId, requestService.getClientIp(request));
        if (user == null)
            return new DefaultReturnable(HttpStatus.UNAUTHORIZED, "Session id is invalid or expired.").getResponseEntity();

        authenticationService.invalidateSession(sessionId);
        return new DefaultReturnable("Successfully logged out.").getResponseEntity();
    }

    @PostMapping("/check-session")
    public ResponseEntity<DefaultReturnable> checkSession(@RequestHeader(value = "Authorization") String sessionId, HttpServletRequest request) {
        sessionId = sessionId.split(" ")[sessionId.split(" ").length - 1];
        return authenticationService.checkSession(sessionId, requestService.getClientIp(request)).getResponseEntity();
    }


}
