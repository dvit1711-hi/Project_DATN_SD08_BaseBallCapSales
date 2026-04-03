package com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PosCustomerGetDto {
    private Integer accountId;
    private String username;
    private String email;
    private String phoneNumber;
    private String displayName;
}