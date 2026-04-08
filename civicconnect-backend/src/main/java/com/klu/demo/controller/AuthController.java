package com.klu.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import com.klu.demo.service.AuthService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

// ✅ FIXED — was com.klu.dto.OTPRequest (wrong package)
import com.klu.demo.dto.OTPRequest;

import java.util.HashMap;
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
@CrossOrigin(origins = {
	    "http://localhost:5173",
	    "https://civic-connect-raoh.onrender.com"
	})
public class AuthController {

    private final UserRepository userRepository;

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    private AuthService authService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // ─── Signup ───────────────────────────────────────────────────
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

        // ✅ BCrypt password match
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

    // ─── Forgot Password ──────────────────────────────────────────
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

    // ─── Verify OTP ───────────────────────────────────────────────
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

    // ─── Reset Password ───────────────────────────────────────────
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