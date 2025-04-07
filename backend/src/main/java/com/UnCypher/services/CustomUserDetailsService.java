package com.UnCypher.security;

import com.UnCypher.repo.AuthRepo;
import com.UnCypher.models.AuthCred;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

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
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("🔎 [UserDetailsService] Looking up: " + email);

        AuthCred cred = authRepo.findByEmail(email);

        if (cred == null) {
            System.out.println("❌ [UserDetailsService] User not found: " + email);
            throw new UsernameNotFoundException("User not found: " + email);
        }

        System.out.println("✅ [UserDetailsService] Found user: " + cred.getEmail());

        List<SimpleGrantedAuthority> authorities = cred.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role)) // Spring requires ROLE_ prefix
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(
                cred.getEmail(),
                cred.getPassword(),
                authorities
        );
    }
}
