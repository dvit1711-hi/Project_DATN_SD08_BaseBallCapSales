package com.example.project_datn_sd08_baseballcapsales.Controller;

import com.example.project_datn_sd08_baseballcapsales.Service.auth.AuthService;
import com.example.project_datn_sd08_baseballcapsales.payload.request.ForgotPasswordRequest;
import com.example.project_datn_sd08_baseballcapsales.payload.request.ResetPasswordByOtpRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/forgot-password/send-otp")
    public ResponseEntity<?> sendOtp(@Valid @RequestBody ForgotPasswordRequest request) {
        return ResponseEntity.ok(Map.of(
                "message", authService.sendForgotPasswordOtp(request.getEmail())
        ));
    }

    @PostMapping("/forgot-password/reset")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordByOtpRequest request) {
        return ResponseEntity.ok(Map.of(
                "message",
                authService.resetPasswordByOtp(
                        request.getEmail(),
                        request.getOtp(),
                        request.getNewPassword(),
                        request.getConfirmPassword()
                )
        ));
    }
}