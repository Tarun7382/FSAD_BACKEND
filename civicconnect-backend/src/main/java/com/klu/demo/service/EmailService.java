package com.klu.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class EmailService {

    @Value("${brevo.api.key:}")
    private String brevoApiKey;

    // 👉 IMPORTANT: use a VERIFIED email from Brevo
    private final String fromEmail = "sravantarungemini@gmail.com";

    public void sendOtp(String toEmail, String otp) {

        try {
            // 🔒 Check API key
            if (brevoApiKey == null || brevoApiKey.isEmpty()) {
                System.out.println("❌ Brevo API key missing");
                return;
            }

            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("api-key", brevoApiKey);

            Map<String, Object> body = Map.of(
                    "sender", Map.of(
                            "name", "Civic Connect",
                            "email", fromEmail
                    ),
                    "to", new Object[]{
                            Map.of("email", toEmail)
                    },
                    "subject", "Civic Connect - Email Verification OTP",
                    "textContent",
                            "Hello,\n\n" +
                            "Your OTP for Civic Connect is: " + otp + "\n\n" +
                            "Valid for 10 minutes. Do not share it with anyone.\n\n" +
                            "— Civic Connect Team"
            );

            HttpEntity<Map<String, Object>> request =
                    new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    "https://api.brevo.com/v3/smtp/email",
                    request,
                    String.class
            );

            System.out.println("✅ OTP sent. Brevo response: " + response.getStatusCode());

        } catch (Exception e) {
            System.out.println("❌ Error sending OTP:");
            e.printStackTrace(); // prevents app crash
        }
    }
}