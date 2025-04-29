package com.UnCypher.services;

import com.UnCypher.models.AuthCred;
import com.UnCypher.repo.AuthRepo;
import com.UnCypher.security.JwtUtil;
import com.UnCypher.models.dto.JwtAuthenticationResponse;
import org.springframework.security.core.Authentication;
import com.UnCypher.utils.PasswordBreachChecker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


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
        if (authRepo.findByEmail(cred.getEmail()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists!");
        }

        if (!cred.getEmail().equalsIgnoreCase("testacc@gmail.com")) {
            boolean breached = PasswordBreachChecker.isBreached(cred.getPassword());
            if (breached) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("❌ That password is too common or has been exposed in a data breach. Please choose a stronger password.");
            }
        }

        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        String hashedPassword = encoder.encode(cred.getPassword());
        cred.setPassword(hashedPassword);
        cred.setUserId(UUID.randomUUID().toString());
        cred.setRoles(List.of("USER"));

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
            authenticationManager.authenticate(authRequest);

            AuthCred auth = authRepo.findByEmail(email);
            if (auth == null) throw new BadCredentialsException("User not found");

            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            String accessToken = jwtUtil.generateAccessToken(auth.getUserId(), userDetails);
            String refreshToken = jwtUtil.generateRefreshToken(auth.getUserId(), userDetails);

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
        String userId = jwtUtil.extractUserId(refreshToken); // ✅ FIXED

        UserDetails userDetails = userDetailsService.loadUserByUsername(userId); // assuming userId is used here
        if (!jwtUtil.isTokenValid(refreshToken, userDetails)) {
            throw new BadCredentialsException("Invalid refresh token");
        }

        String newAccessToken = jwtUtil.generateAccessToken(userId, userDetails);
        return new JwtAuthenticationResponse(newAccessToken, refreshToken);
    }
}


