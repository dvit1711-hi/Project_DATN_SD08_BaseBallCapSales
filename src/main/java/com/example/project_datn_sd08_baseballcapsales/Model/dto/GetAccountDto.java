package com.example.project_datn_sd08_baseballcapsales.Model.dto;

import com.example.project_datn_sd08_baseballcapsales.Model.entity.Account;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.Address;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetAccountDto {
    @Size(max = 50)
    @NotNull
    @Nationalized
    private String accountCode;

    @Size(max = 50)
    @NotNull
    @Nationalized
    private String username;

    @Size(max = 255)
    @NotNull
    @Nationalized
    private String password;

    @Size(max = 100)
    @Nationalized
    private String email;

    @Size(max = 20)
    @Nationalized
    private String phoneNumber;

    @Size(max = 255)
    @Nationalized
    private String images;

    @ColumnDefault("getdate()")
    private Instant createDate;

    public GetAccountDto(Account account) {
        this.accountCode = account.getAccountCode();
        this.username = account.getUsername();
        this.password = account.getPassword();
        this.email = account.getEmail();
        this.phoneNumber = account.getPhoneNumber();
        this.images = account.getImages();
        this.createDate = account.getCreateDate();
    }
}
