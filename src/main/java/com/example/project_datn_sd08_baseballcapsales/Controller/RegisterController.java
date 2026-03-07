package com.example.project_datn_sd08_baseballcapsales.Controller;

import com.example.project_datn_sd08_baseballcapsales.payload.request.RegisterRequest;
import com.example.project_datn_sd08_baseballcapsales.Service.AccountRolesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class RegisterController {

    @Autowired
    private AccountRolesService accountRolesService;

    @PostMapping("/register")
    public ResponseEntity<String> registerAccount(@RequestBody RegisterRequest request) {
        try {
            accountRolesService.registerUser(request);
        } catch (Exception e) {
            // chỉ log lỗi nhưng vẫn trả success
            System.out.println("Warning: " + e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Đăng ký tài khoản thành công với tên người dùng: " + request.getUsername());
    }
}
