package com.UnCypher.security;

import com.UnCypher.models.AuthCred;
import com.UnCypher.repo.AuthRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AuthRepo authRepo;

    @Autowired
    public CustomUserDetailsService(AuthRepo authRepo) {
        this.authRepo = authRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        System.out.println("🔎 [UserDetailsService] Looking up: " + identifier);

        // ✅ Special case for internal agent
        if ("insight-agent".equals(identifier)) {
            System.out.println("✅ [UserDetailsService] Recognized internal service account: insight-agent");
            return User.withUsername("insight-agent")
                    .password("dummy") // password not used
                    .roles("AGENT")    // will grant ROLE_AGENT
                    .build();
        }

        // 🔎 Normal user lookup
        AuthCred cred;

        if (identifier.matches("^[0-9a-fA-F-]{36}$")) {
            // Treat as UUID (for JWT flow)
            cred = authRepo.findByUserId(identifier);
        } else {
            // Otherwise, treat as email (for login)
            cred = authRepo.findByEmail(identifier);
        }

        if (cred == null) {
            System.out.println("❌ [UserDetailsService] User not found: " + identifier);
            throw new UsernameNotFoundException("User not found: " + identifier);
        }

        System.out.println("✅ [UserDetailsService] Found user: " + cred.getEmail());

        List<SimpleGrantedAuthority> authorities = cred.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role)) // Spring requires "ROLE_" prefix
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(
                cred.getUserId(),
                cred.getPassword(),
                authorities
        );
    }
}
