package com.UnCypher;
import org.springframework.context.annotation.Import;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.UnCypher.config.SecurityConfig; // ✅ Import SecurityConfig

@SpringBootApplication
@Import(SecurityConfig.class)
public class BackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }
}
