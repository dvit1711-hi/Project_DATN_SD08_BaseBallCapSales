package com.example.project_datn_sd08_baseballcapsales.payload.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CreateStaffAccountRequest {
    private String username;
    private String email;
    private String phoneNumber;
    private String password;
    private String images;
    private Integer statusID;
}