package com.klu.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.klu.demo.model.User;
import com.klu.demo.repository.UserRepository;

@SpringBootApplication
public class CivicconnectBackendApplication {

    private final BCryptPasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder();

    public static void main(String[] args) {
        SpringApplication.run(CivicconnectBackendApplication.class, args);
    }

    @Bean
    CommandLineRunner init(UserRepository userRepository) {
        return args -> {
            if (!userRepository.existsByUsernameAndRole("admin", "admin")) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole("admin");

                userRepository.save(admin);
                System.out.println("✅ Admin seeded successfully");
            }
        };
    }
}