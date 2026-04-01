package com.example.project_datn_sd08_baseballcapsales.payload.reponse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class LoginResponse {
    private String accessToken;
    private Integer accountId;
    private String username;
    private String email;
    private Set<String> roles;
}
