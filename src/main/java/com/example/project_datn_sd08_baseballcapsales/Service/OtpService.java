package com.example.project_datn_sd08_baseballcapsales.Service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class OtpService {

    private final Map<String, String> otpCache = new HashMap<>();

    public void saveOtp(String email, String otp) {
        otpCache.put(email, otp);
    }

    public boolean verifyOtp(String email, String otp) {
        return otp.equals(otpCache.get(email));
    }
}
