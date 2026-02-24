package com.example.project_datn_sd08_baseballcapsales.Model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.Nationalized;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostColorDto {
    @NotBlank
    @Size(max = 50)
    @Nationalized
    private String colorName;

    @Size(max = 20)
    @Nationalized
    private String colorCode;
}
