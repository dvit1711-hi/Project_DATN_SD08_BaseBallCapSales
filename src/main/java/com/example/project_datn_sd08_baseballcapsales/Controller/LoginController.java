package com.example.project_datn_sd08_baseballcapsales.Controller;

import com.example.project_datn_sd08_baseballcapsales.payload.request.LoginRequest;
import com.example.project_datn_sd08_baseballcapsales.payload.reponse.LoginResponse;
import com.example.project_datn_sd08_baseballcapsales.Service.auth.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class LoginController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response){
        LoginResponse loginResponse=authService.login(loginRequest);
        String accessToken=loginResponse.getAccessToken();
        ResponseCookie accessCookie = ResponseCookie.from("access_token", accessToken)
                .httpOnly(true)
                .secure(false) // dev
                .path("/")
                .maxAge(Duration.ofDays(1))
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE,accessCookie.toString());

        return ResponseEntity.ok(Map.of("message", "Đăng nhập thành công"));
    }
}

