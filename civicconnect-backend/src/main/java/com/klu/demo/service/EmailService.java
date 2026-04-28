package com.klu.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendOtp(String toEmail, String otp) throws Exception {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Civic Connect - Email Verification OTP");
        message.setText(
            "Hello,\n\n" +
            "Your OTP for Civic Connect is: " + otp + "\n\n" +
            "Valid for 10 minutes. Do not share it with anyone.\n\n" +
            "— Civic Connect Team"
        );
        mailSender.send(message);
        System.out.println("✅ OTP email sent to: " + toEmail);
    }
}