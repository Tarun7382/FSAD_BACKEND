package com.klu.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.klu.demo.dto.ForgotPasswordRequest;
import com.klu.demo.dto.OTPRequest;
import com.klu.demo.dto.ResetPasswordRequest;
import com.klu.demo.model.User;
import com.klu.demo.repository.UserRepository;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    // ✅ Direct instantiation — no @Bean needed
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // ─── Send OTP ─────────────────────────────────────────────────
    public String sendForgotOtp(ForgotPasswordRequest request) throws Exception {

        System.out.println("sendForgotOtp >> email:[" + request.getEmail() + "]");

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException(
                        "No account found with email: " + request.getEmail()));

        if (!user.getRole().equalsIgnoreCase("citizen")) {
            return "Only citizens can reset password";
        }

        // Generate 6-digit OTP
        String otp = String.valueOf((int) (Math.random() * 900000) + 100000);

        user.setOtp(otp);
        userRepository.save(user);

        emailService.sendOtp(user.getEmail(), otp);

        System.out.println("OTP saved and sent >> email:[" + user.getEmail()
                + "] otp:[" + otp + "]");

        return "OTP sent successfully to " + user.getEmail();
    }

    // ─── Verify OTP ───────────────────────────────────────────────
    // ✅ This is what AuthController calls
    public String __verifyForgotOtp__(OTPRequest request) {
        System.out.println("__verifyForgotOtp__ >> email:["
                + request.getEmail() + "] otp:[" + request.getOtp() + "]");

        if (request.getEmail() == null || request.getOtp() == null) {
            throw new RuntimeException("Email and OTP are required");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException(
                        "No account found with email: " + request.getEmail()));

        System.out.println("DB OTP:[" + user.getOtp()
                + "] Input OTP:[" + request.getOtp() + "]");

        if (user.getOtp() == null) {
            throw new RuntimeException(
                    "OTP expired or not requested. Please request a new OTP.");
        }

        if (user.getOtp().trim().equals(request.getOtp().trim())) {
            // ✅ Don't clear OTP yet — reset-password step needs the email verified
            return "OTP verified successfully";
        }

        throw new RuntimeException("Invalid OTP. Please try again.");
    }
    
 // ─── Send Signup OTP ──────────────────────────────────────────
    public String sendSignupOtp(ForgotPasswordRequest request) throws Exception {
        System.out.println("sendSignupOtp >> email:[" + request.getEmail() + "]");

        // Check if email already registered
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered. Please login.");
        }

        // Generate 6-digit OTP
        String otp = String.valueOf((int) (Math.random() * 900000) + 100000);

        // Store OTP temporarily in a temp user or in-memory
        // We reuse a temp User object with just email + otp
        User tempUser = userRepository.findByEmail(request.getEmail())
                .orElse(new User());
        tempUser.setEmail(request.getEmail());
        tempUser.setOtp(otp);

        // Save temp record to reuse OTP verify logic
        userRepository.save(tempUser);

        emailService.sendOtp(request.getEmail(), otp);

        System.out.println("Signup OTP sent >> email:[" + request.getEmail()
                + "] otp:[" + otp + "]");

        return "OTP sent to " + request.getEmail();
    }

    // ─── Verify Signup OTP ────────────────────────────────────────
    public String verifySignupOtp(OTPRequest request) {
        System.out.println("verifySignupOtp >> email:[" + request.getEmail()
                + "] otp:[" + request.getOtp() + "]");

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException(
                        "No OTP request found for: " + request.getEmail()));

        if (user.getOtp() == null) {
            throw new RuntimeException("OTP expired. Please request a new one.");
        }

        if (user.getOtp().trim().equals(request.getOtp().trim())) {
            user.setOtp(null); // ✅ Clear OTP after verification
            userRepository.save(user);
            return "Email verified successfully";
        }

        throw new RuntimeException("Invalid OTP. Please try again.");
    }

    // ─── Reset Password ───────────────────────────────────────────
    public String resetPassword(ResetPasswordRequest request) {
        System.out.println("resetPassword >> email:[" + request.getEmail() + "]");

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException(
                        "No account found with email: " + request.getEmail()));

        // ✅ Hash new password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setOtp(null); // ✅ Clear OTP after successful reset
        userRepository.save(user);

        System.out.println("Password reset successful for: " + user.getEmail());
        return "Password reset successful";
    }
}