package com.example.project_datn_sd08_baseballcapsales.Model.dto.PutDto;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
public class PutAccountDto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
}
