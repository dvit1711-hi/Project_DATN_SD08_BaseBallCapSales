package com.example.project_datn_sd08_baseballcapsales.payload.reponse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CreateStaffAccountResponse {
    private Integer accountID;
    private String username;
    private String email;
    private String phoneNumber;
    private Integer statusID;
    private String statusName;
    private String roleName;
}