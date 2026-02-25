package com.example.project_datn_sd08_baseballcapsales.Model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostAccountDto {
    @Size(max = 50)
    @NotNull
    @Nationalized
    private String accountCode;

    @NotNull
    @Nationalized
    private String username;

    @Size(max = 255)
    @NotNull
    @Nationalized
    private String password;
}
