package com.UnCypher.controllers;

import com.UnCypher.services.AuthService;
import com.UnCypher.models.AuthCred;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService service;

    @Autowired
    public AuthController(AuthService service){
        this.service = service;
    }

    @PostMapping("/signup")
    public ResponseEntity<String> registerUser(@RequestBody AuthCred cred){
        return service.registerUser(cred);
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody AuthCred cred, HttpServletRequest request){
        if(service.loginUser(cred.getEmail(), cred.getPassword(), request)){
            return ResponseEntity.ok("Login successful!");
        }
        else{
            //invalid
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Invalid user name or password!");
        }
    }
    @PostMapping("/logout")
    public ResponseEntity<String> logoutUser(HttpServletRequest request, HttpServletResponse response){
        return service.logoutUser(request, response);
    }
}

