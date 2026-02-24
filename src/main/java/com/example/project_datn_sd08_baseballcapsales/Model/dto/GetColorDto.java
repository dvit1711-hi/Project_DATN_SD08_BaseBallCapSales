package com.example.project_datn_sd08_baseballcapsales.Model.dto;

import com.example.project_datn_sd08_baseballcapsales.Model.entity.Color;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.Nationalized;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetColorDto {
    @NotBlank
    @Size(max = 50)
    @Nationalized
    private String colorName;

    @Size(max = 20)
    @Nationalized
    private String colorCode;

    public GetColorDto(Color color) {
        this.colorName = color.getColorName();
        this.colorCode = color.getColorCode();
    }
}
