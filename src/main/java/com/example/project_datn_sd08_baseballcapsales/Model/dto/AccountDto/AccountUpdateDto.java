package com.example.project_datn_sd08_baseballcapsales.Model.dto.AccountDto;

import lombok.Data;

@Data
public class AccountUpdateDto {
    private int accountId;
    private String images;
    private String username;
    private String email;
    private String phoneNumber;

    private String unitNumber;
    private String streetNumber;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String region;
    private String postalCode;
}
