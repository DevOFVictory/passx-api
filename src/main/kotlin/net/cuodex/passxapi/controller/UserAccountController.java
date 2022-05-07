package net.cuodex.passxapi.controller;

import net.cuodex.passxapi.dto.*;
import net.cuodex.passxapi.returnables.DefaultReturnable;
import net.cuodex.passxapi.service.AuthenticationService;
import net.cuodex.passxapi.service.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/account")
public class UserAccountController {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private UserAccountService userAccountService;


    @DeleteMapping("")
    public ResponseEntity<DefaultReturnable> deleteUser(@RequestBody DeleteAccountDto deleteAccDto){
        return authenticationService.deleteUser(deleteAccDto.getSessionId(), deleteAccDto.getPasswordTest()).getResponseEntity();
    }

    @GetMapping("/information")
    public ResponseEntity<DefaultReturnable> getUserInformation(@RequestBody SessionDto sessionDto){
        return userAccountService.getInformation(sessionDto.getSessionId()).getResponseEntity();
    }


    @PutMapping("/information")
    public ResponseEntity<DefaultReturnable> updateUserInformation(@RequestBody UpdateUserInformationDto updateUserInformationDto){
        return userAccountService.updateInformation(updateUserInformationDto.getSessionId(), updateUserInformationDto.getPasswordTest(), updateUserInformationDto.getData()).getResponseEntity();
    }

    @PatchMapping("/change-password")
    public ResponseEntity<DefaultReturnable> updateUserInformation(@RequestBody ChangePasswordDto changePasswordDto){
        return userAccountService.changePassword(changePasswordDto.getSessionId(), changePasswordDto.getPasswordTest(), changePasswordDto.getNewPasswordTest()).getResponseEntity();
    }
}