package com.example.AuthService.controller;

import com.example.AuthService.domain.User;
import com.example.AuthService.dto.LoginRequestDTO;
import com.example.AuthService.dto.PrivilegeChangeDTO;
import com.example.AuthService.dto.UserDTO;
import com.example.AuthService.exception.CustomException;
import com.example.AuthService.service.UserService;
import com.netflix.ribbon.proxy.annotation.Http;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.regex.Pattern;

import static com.example.AuthService.security.SecurityConstants.TOKEN_BEARER_PREFIX;

@RestController
public class UserController {

    private static final Pattern emailPattern = Pattern.compile("^[a-zA-Z0-9._-]+@(.+)$");
    private static final Pattern passwordPattern = Pattern.compile("^(?=.*[0-9])(?=.*[A-Z])(?=.*[a-z])[a-zA-Z0-9!@#$%^&*]{6,25}$");
    private static final Pattern namePattern = Pattern.compile("^[a-zA-Z '-]+$");


    @Autowired
    private UserService userService;


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        try {
            if(loginRequestDTO.getUsername().equals("") || loginRequestDTO.getPassword().equals("")) {
                throw new CustomException("Fields must not be empty.", HttpStatus.NOT_ACCEPTABLE);
            }else if ( !emailPattern.matcher( loginRequestDTO.getUsername() ).matches()) {
                throw new CustomException( "Improper email format.", HttpStatus.NOT_ACCEPTABLE);
            }
            return new ResponseEntity<String>(userService.login(loginRequestDTO), HttpStatus.OK);
        }
        catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Bad request", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/user")
    public ResponseEntity<?> register(@RequestBody UserDTO userDTO) {
        try {
            if(userDTO.getEmail().equals("") || userDTO.getFirstName().equals("") || userDTO.getLastName().equals("") || userDTO.getPassword().equals("") || userDTO.getRoleName().equals("")){
                throw new CustomException("Fields must not be empty.", HttpStatus.NOT_ACCEPTABLE);
            }else if ( !emailPattern.matcher( userDTO.getEmail() ).matches()) {
                throw new CustomException( "Improper email format.", HttpStatus.NOT_ACCEPTABLE);
            }else if(!passwordPattern.matcher( userDTO.getPassword()).matches()){
                throw new CustomException("Password doesn't match requirements.", HttpStatus.NOT_ACCEPTABLE);
            }else if(!namePattern.matcher(userDTO.getFirstName()).matches()){
                throw new CustomException("First name doesn't match reqirements", HttpStatus.NOT_ACCEPTABLE);
            }else if(!namePattern.matcher(userDTO.getLastName()).matches()){
                throw new CustomException("Last name doesn't match reqirements", HttpStatus.NOT_ACCEPTABLE);
            }

            return new ResponseEntity<UserDTO>(userService.register(userDTO), HttpStatus.OK);
        } catch (CustomException e){
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), e.getHttpStatus());
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Bad request", HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("/user/{email}")
    @PreAuthorize("hasAuthority('PROFILE_VIEWING')")
    public ResponseEntity<?> getUserProfile(@PathVariable String email, Authentication authentication) {
        try {
            if(email.equals("") || !emailPattern.matcher(email).matches()){
                throw new CustomException( "Improper email format.", HttpStatus.NOT_ACCEPTABLE);
            }
            return new ResponseEntity<User>(userService.getUser(email, authentication), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Bad request", HttpStatus.BAD_REQUEST);
        }
    }


    @PostMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestBody String bearerToken) {
        try {
            String accessBearerToken = TOKEN_BEARER_PREFIX + userService.verifyUser(bearerToken);
            return new ResponseEntity<String>(accessBearerToken, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Bad request", HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping
    @PreAuthorize("hasAuthority('ENDUSER_PERMISION_CHANGING')")
    public ResponseEntity<?> changeUserPrivileges(@RequestBody PrivilegeChangeDTO privilegeChangeDTO) {
        try {
            return new ResponseEntity<Boolean>(userService.changeUserPrivileges(privilegeChangeDTO.getPrivilegeList(), privilegeChangeDTO.getEnduserId(), privilegeChangeDTO.isRemove()), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<String>("Bad request", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    @PreAuthorize("hasAuthority('REQUEST_CREATING')")
    public ResponseEntity<?> sayHelloEndUser() {
        try {
            System.out.println("HELLO BY ENDUSER");
            return new ResponseEntity<String>("Very Good, you are not blocked", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<String>("Bad request", HttpStatus.BAD_REQUEST);
        }
    }

}
