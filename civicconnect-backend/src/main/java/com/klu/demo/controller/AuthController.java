package com.klu.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import com.klu.demo.service.AuthService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.klu.demo.dto.OTPRequest;

import java.util.Map;
import java.util.Optional;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.klu.demo.dto.ForgotPasswordRequest;
import com.klu.demo.dto.ResetPasswordRequest;
import com.klu.demo.model.User;
import com.klu.demo.repository.UserRepository;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    private AuthService authService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody User user) {
        user.setUsername(user.getUsername().trim());
        user.setPassword(passwordEncoder.encode(user.getPassword().trim()));
        user.setRole(user.getRole().toLowerCase().trim());

        if (userRepository.existsByUsernameAndRole(
                user.getUsername(), user.getRole())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Username already exists"));
        }

        userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "Signup successful"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginUser) {

        if (loginUser.getUsername() == null ||
            loginUser.getPassword() == null ||
            loginUser.getRole() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Missing username, password, or role"));
        }

        String username = loginUser.getUsername().trim();
        String password = loginUser.getPassword().trim();
        String role = loginUser.getRole().toLowerCase().trim();

        System.out.println("LOGIN >> username:[" + username + "] role:[" + role + "]");

        Optional<User> userOpt =
                userRepository.findByUsernameAndRole(username, role);

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            if (passwordEncoder.matches(password, user.getPassword())) {
                return ResponseEntity.ok(Map.of(
                        "message", "Login successful",
                        "username", user.getUsername(),
                        "role", user.getRole()
                ));
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid password"));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", "User not found or role mismatch"));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(
            @RequestBody ForgotPasswordRequest request) {
        try {
            String result = authService.sendForgotOtp(request);
            return ResponseEntity.ok(Map.of("message", result));
        } catch (Exception e) {
            System.err.println("Forgot password error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to send OTP: " + e.getMessage()));
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody OTPRequest request) {
        try {
            System.out.println("Verify OTP >> email:[" + request.getEmail()
                    + "] otp:[" + request.getOtp() + "]");

            String result = authService.__verifyForgotOtp__(request);
            return ResponseEntity.ok(Map.of("message", result));
        } catch (Exception e) {
            System.err.println("Verify OTP error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Invalid or expired OTP"));
        }
    }

    @PostMapping("/signup/send-otp")
    public ResponseEntity<?> sendSignupOtp(
            @RequestBody ForgotPasswordRequest request) {
        try {
            String result = authService.sendSignupOtp(request);
            return ResponseEntity.ok(Map.of("message", result));
        } catch (Exception e) {
            System.err.println("Signup OTP error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/signup/verify-otp")
    public ResponseEntity<?> verifySignupOtp(@RequestBody OTPRequest request) {
        try {
            String result = authService.verifySignupOtp(request);
            return ResponseEntity.ok(Map.of("message", result));
        } catch (Exception e) {
            System.err.println("Signup verify OTP error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(
            @RequestBody ResetPasswordRequest request) {
        try {
            String result = authService.resetPassword(request);
            return ResponseEntity.ok(Map.of("message", result));
        } catch (Exception e) {
            System.err.println("Reset password error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to reset password"));
        }
    }
}