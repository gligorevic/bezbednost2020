package com.example.AuthService.controller;

import com.example.AuthService.domain.Privilege;
import com.example.AuthService.dto.LoginRequestDTO;
import com.example.AuthService.dto.PrivilegeChangeDTO;
import com.example.AuthService.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.List;

import static com.example.AuthService.security.SecurityConstants.TOKEN_BEARER_PREFIX;

@RestController
public class UserController {

    @Autowired
    private UserService userService;


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        try {
            return new ResponseEntity<String>(userService.login(loginRequestDTO), HttpStatus.OK);
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
