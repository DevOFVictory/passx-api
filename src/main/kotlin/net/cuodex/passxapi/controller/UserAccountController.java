package net.cuodex.passxapi.controller;

import net.cuodex.passxapi.dto.LoginDto;
import net.cuodex.passxapi.dto.RegisterDto;
import net.cuodex.passxapi.entity.Role;
import net.cuodex.passxapi.entity.UserAccount;
import net.cuodex.passxapi.repository.RoleRepository;
import net.cuodex.passxapi.repository.UserAccountRepository;
import net.cuodex.passxapi.returnables.DefaultReturnable;
import net.cuodex.passxapi.utils.AuthenticationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/account")
public class UserAccountController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserAccountRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;


    @PostMapping("/login")
    public DefaultReturnable authenticateUser(@RequestBody LoginDto loginDto){
        System.out.println("logged in");
        String sessionId = authenticationManager.authenticate(
                loginDto.getUsernameOrEmail(), loginDto.getPassword());

        DefaultReturnable returnable = new DefaultReturnable(HttpStatus.OK.value(), "Successfully logged in.");
        returnable.addData("sessionId", sessionId);

        return returnable;
    }

    @PostMapping("/create")
    public DefaultReturnable registerUser(@RequestBody RegisterDto signUpDto){

        System.out.println("called");

        String username = signUpDto.getUsername();
        String email = signUpDto.getEmail();
        String passwordTest = signUpDto.getPassword();


        if (!(username.matches("^[a-zA-Z0-9_]*$") && username.length() >= 3 && username.length() <= 16))

            return new DefaultReturnable(HttpStatus.BAD_REQUEST.value(), "Invalid username.");


        if (!email.matches("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])"))
            return new DefaultReturnable(HttpStatus.BAD_REQUEST.value(), "Invalid email address.");


        if (!passwordTest.matches("^[a-zA-Z0-9=]*$"))
            return new DefaultReturnable(HttpStatus.BAD_REQUEST.value(), "Invalid password test.");

        // add check for username exists in a DB
        if(userRepository.existsByUsername(signUpDto.getUsername())){
            return new DefaultReturnable(HttpStatus.BAD_REQUEST.value(), "Username already taken.");
        }


        // create user object
        UserAccount user = new UserAccount();
        user.setUsername(signUpDto.getUsername());
        user.setEmail(signUpDto.getEmail());
        user.setPassword(signUpDto.getPassword());

        Role roles = roleRepository.findByName("USER").get();
        user.setRoles(Collections.singleton(roles));

        userRepository.save(user);

        return new DefaultReturnable("User successfully created.").addData("user", user);

    }
}