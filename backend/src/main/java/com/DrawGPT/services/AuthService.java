package com.UnCypher.services;

import com.UnCypher.models.AuthCred;
import com.UnCypher.repo.AuthRepo;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class AuthService{
    private AuthRepo repo;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    public void AuthService(AuthRepo repo){
        this.repo = repo;
    }

    public ResponseEntity<String> registerUser(AuthCred cred){
        if(repo.findByEmail(cred.getEmail())!=null){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists!");
        }
        String hashedPassword = passwordEncoder.encode(cred.getPassword());
        cred.setPassword(hashedPassword);
        repo.saveCredentials(cred);
        return ResponseEntity.ok("User registered successfully!");
    }

    public boolean loginUser(String email, String password){
        AuthCred cred = repo.findByEmail(email);
        if(cred==null){
            return false;
        }
        return passwordEncoder.matches(password, cred.getPassword());
    }
}