package com.example.project_datn_sd08_baseballcapsales.Controller;

import com.example.project_datn_sd08_baseballcapsales.Service.EmailService;
import com.example.project_datn_sd08_baseballcapsales.Service.OtpService;
import com.example.project_datn_sd08_baseballcapsales.payload.request.RegisterRequest;
import com.example.project_datn_sd08_baseballcapsales.Service.AccountRolesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class RegisterController {

    @Autowired
    private AccountRolesService accountRolesService;
    @Autowired
    private OtpService otpService;
    @Autowired
    private EmailService emailService;

    @PostMapping("/register/request-otp")
    public ResponseEntity<?> requestOtp(@RequestBody Map<String, String> body) {

        String email = body.get("email");

        String otp = String.valueOf((int) ((Math.random() * 900000) + 100000));

        otpService.saveOtp(email, otp);

        emailService.sendOtp(email, otp);

        return ResponseEntity.ok(Map.of(
                "message", "OTP đã gửi tới email " + email
        ));
    }

    @PostMapping("/register/confirm")
    public ResponseEntity<?> confirmRegister(@RequestBody RegisterRequest request) {

        boolean verified = otpService.verifyOtp(request.getEmail(), request.getOtp());

        if (!verified) {
            return ResponseEntity.badRequest().body("OTP không hợp lệ");
        }

        accountRolesService.registerUser(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Đăng ký tài khoản thành công cho email: " + request.getEmail());
    }
}
