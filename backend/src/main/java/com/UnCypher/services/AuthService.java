package com.UnCypher.services;

import com.UnCypher.models.AuthCred;
import com.UnCypher.repo.AuthRepo;
import com.UnCypher.security.JwtUtil;
import com.UnCypher.security.JwtAuthenticationResponse;
import com.UnCypher.utils.PasswordBreachChecker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;



import java.util.List;

@Service
public class AuthService {

    private final AuthRepo authRepo;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthService(AuthRepo authRepo,
                       AuthenticationManager authenticationManager,
                       UserDetailsService userDetailsService,
                       JwtUtil jwtUtil) {
        this.authRepo = authRepo;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }

    public ResponseEntity<String> registerUser(AuthCred cred) {
        // 1️⃣ Duplicate check
        if (authRepo.findByEmail(cred.getEmail()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists!");
        }

        // 2️⃣ Password breach check (skip for dev)
        if (!cred.getEmail().equalsIgnoreCase("testacc@gmail.com")) {
            boolean breached = PasswordBreachChecker.isBreached(cred.getPassword());
            if (breached) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("❌ That password is too common or has been exposed in a data breach. Please choose a stronger password.");
            }
        }

        // 3️⃣ Hash password
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        String hashedPassword = encoder.encode(cred.getPassword());
        cred.setPassword(hashedPassword);

        // 4️⃣ Set roles
        cred.setRoles(List.of("USER")); // ✅ Correct format

        // 5️⃣ Save to DB
        try {
            authRepo.saveCredentials(cred);
            return ResponseEntity.ok("User registered successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Signup failed: " + e.getMessage());
        }
    }

    public JwtAuthenticationResponse login(String email, String password) {
        try {
            System.out.println("🔐 [AuthService] Login attempt for: " + email);

            Authentication authRequest = new UsernamePasswordAuthenticationToken(email, password);
            authenticationManager.authenticate(authRequest);  // 💥 This is where it fails if user not found

            System.out.println("✅ [AuthService] AuthenticationManager passed");

            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            System.out.println("✅ [AuthService] Loaded user: " + userDetails.getUsername());

            String accessToken = jwtUtil.generateAccessToken(userDetails);
            String refreshToken = jwtUtil.generateRefreshToken(userDetails);

            return new JwtAuthenticationResponse(accessToken, refreshToken);
        } catch (BadCredentialsException ex) {
            System.out.println("❌ [AuthService] Bad credentials for email: " + email);
            throw new BadCredentialsException("Invalid email or password");
        } catch (Exception ex) {
            System.out.println("❌ [AuthService] General error during login: " + ex.getMessage());
            ex.printStackTrace();
            throw new RuntimeException("Login failed due to server error");
        }
    }


    public JwtAuthenticationResponse refreshToken(String refreshToken) {
        String username = jwtUtil.extractUsername(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (!jwtUtil.isTokenValid(refreshToken, userDetails)) {
            throw new BadCredentialsException("Invalid refresh token");
        }

        String newAccessToken = jwtUtil.generateAccessToken(userDetails);
        return new JwtAuthenticationResponse(newAccessToken, refreshToken);
    }
}

