package com.UnCypher.services;

import com.UnCypher.repo.UserRepo;
import com.UnCypher.models.User;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class UserService {
    private final UserRepo repo;

    @Autowired
    public UserService(UserRepo repo) {
        this.repo = repo;
    }

    public ResponseEntity<String> saveUser(User user) {
        repo.saveUser(user); // Fixed incorrect variable
        return ResponseEntity.ok("User registered successfully!");
    }

    public User getUser(String email) {
        return repo.findByEmail(email);
    }
}
