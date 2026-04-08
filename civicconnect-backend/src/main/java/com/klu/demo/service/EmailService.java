package com.klu.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@Service
public class EmailService {

    @Value("${resend.api.key}")
    private String resendApiKey;

    public void sendOtp(String toEmail, String otp) throws Exception {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + resendApiKey);

        Map<String, Object> body = Map.of(
            "from", "onboarding@resend.dev",
            "to", toEmail,
            "subject", "Civic Connect Password Reset OTP",
            "text", "Your OTP is: " + otp + ". Valid for 10 minutes."
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        restTemplate.postForEntity(
            "https://api.resend.com/emails",
            request,
            String.class
        );
    }
}