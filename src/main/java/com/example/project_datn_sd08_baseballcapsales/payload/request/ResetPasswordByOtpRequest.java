package com.example.project_datn_sd08_baseballcapsales.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordByOtpRequest {
    private String email;
    private String otp;
    private String newPassword;
    private String confirmPassword;
}