package com.UnCypher.services;

import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Collections;
import com.UnCypher.models.AuthCred;
import com.UnCypher.repo.AuthRepo;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Cookie;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.context.SecurityContext;


@Service
public class AuthService {
    private AuthRepo repo;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    public AuthService(AuthRepo repo) {
        this.repo = repo;
    }

    public ResponseEntity<String> registerUser(AuthCred cred) {
        if (repo.findByEmail(cred.getEmail()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists!");
        }
        String hashedPassword = passwordEncoder.encode(cred.getPassword());
        cred.setPassword(hashedPassword);
        repo.saveCredentials(cred);
        return ResponseEntity.ok("User registered successfully!");
    }

    public boolean loginUser(String email, String password, HttpServletRequest request) {
        AuthCred cred = repo.findByEmail(email);
        if (cred == null || !passwordEncoder.matches(password, cred.getPassword())) {
            return false;
        }

        // ✅ Create Authentication object using Spring Security's UserDetails
        User userDetails = new User(email, "", Collections.emptyList());
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        // ✅ Create and set SecurityContext
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        // ✅ Store context in session
        request.getSession(true).setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                context
        );

        return true;
    }

    public ResponseEntity<String> logoutUser(HttpServletRequest request, HttpServletResponse response) {
        try {
            request.getSession().invalidate();
            Cookie xsrfCookie = new Cookie("XSRF-TOKEN", null);
            xsrfCookie.setHttpOnly(true);
            xsrfCookie.setPath("/");
            xsrfCookie.setMaxAge(0);

            response.addCookie(xsrfCookie);
            return ResponseEntity.ok("User log out successful!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Logout Failed!");
        }
    }
}