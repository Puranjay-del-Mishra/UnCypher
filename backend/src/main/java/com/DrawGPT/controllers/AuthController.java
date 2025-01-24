package com.UnCypher.controllers;

import com.UnCypher.services.AuthService;
import com.UnCypher.models.AuthCred;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService service;

    public AuthController(AuthService service){
        this.service = service;
    }

    @PostMapping("/signup")
    public ResponseEntity<String> registerUser(@RequestBody AuthCred cred){
        return service.registerUser(cred);
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody AuthCred cred){
        if(service.loginUser(cred.getEmail(), cred.getPassword())){
            return ResponseEntity.ok("Login successful!");
        }
        else{
            //invalid
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Invalid user name or password!");
        }
    }
}