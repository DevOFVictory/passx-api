package net.cuodex.passxapi.controller;

import net.cuodex.passxapi.dto.*;
import net.cuodex.passxapi.returnables.DefaultReturnable;
import net.cuodex.passxapi.service.AuthenticationService;
import net.cuodex.passxapi.service.RequestService;
import net.cuodex.passxapi.service.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@CrossOrigin
@RequestMapping("/account")
public class UserAccountController {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    private RequestService requestService;


    @DeleteMapping("")
    public ResponseEntity<DefaultReturnable> deleteAccount(@RequestHeader(value = "Authorization") String sessionId, HttpServletRequest request) {
        sessionId = sessionId.split(" ")[sessionId.split(" ").length - 1];
        return userAccountService.deleteUser(sessionId, requestService.getClientIp(request)).getResponseEntity();
    }


    @GetMapping("/information")
    public ResponseEntity<DefaultReturnable> getUserInformation(@RequestHeader(value = "Authorization") String sessionId, HttpServletRequest request){
        sessionId = sessionId.split(" ")[sessionId.split(" ").length - 1];
        return userAccountService.getInformation(sessionId, requestService.getClientIp(request)).getResponseEntity();
    }


    @PutMapping("/information")
    public ResponseEntity<DefaultReturnable> updateUserInformation(@RequestHeader(value = "Authorization") String sessionId, @Valid @RequestBody UpdateUserInformationDto updateUserInformationDto, HttpServletRequest request){
        sessionId = sessionId.split(" ")[sessionId.split(" ").length - 1];
        return userAccountService.updateInformation(sessionId, updateUserInformationDto.getPasswordTest(), updateUserInformationDto.getData(), requestService.getClientIp(request)).getResponseEntity();
    }

    @PatchMapping("/change-password")
    public ResponseEntity<DefaultReturnable> updateUserInformation(@RequestHeader(value = "Authorization") String sessionId, @Valid @RequestBody ChangePasswordDto changePasswordDto, HttpServletRequest request){
        sessionId = sessionId.split(" ")[sessionId.split(" ").length - 1];
        return userAccountService.changePassword(sessionId, changePasswordDto.getPasswordTest(), changePasswordDto.getNewPasswordTest(), changePasswordDto.getEntries(), requestService.getClientIp(request)).getResponseEntity();
    }

}