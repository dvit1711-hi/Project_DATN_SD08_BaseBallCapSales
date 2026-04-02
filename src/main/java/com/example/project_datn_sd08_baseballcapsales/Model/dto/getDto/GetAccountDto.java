package com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto;

import com.example.project_datn_sd08_baseballcapsales.Model.entity.Account;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetAccountDto {

    private Integer id;
    private String username;
    private String email;
    private String phoneNumber;
    private String images;
    private Instant createDate;
    private String statusName;
    private List<String> roles;

    public GetAccountDto(Account account) {
        this.id = account.getId();
        this.username = account.getUsername();
        this.email = account.getEmail();
        this.phoneNumber = account.getPhoneNumber();
        this.images = account.getImages();
        this.createDate = account.getCreateDate();

        if (account.getStatus() != null) {
            this.statusName = account.getStatus().getStatusName();
        }

        if (account.getAccountRoles() != null) {
            this.roles = account.getAccountRoles().stream()
                    .map(accountRole -> accountRole.getRole().getRoleName())
                    .collect(Collectors.toList());
        }
    }
}