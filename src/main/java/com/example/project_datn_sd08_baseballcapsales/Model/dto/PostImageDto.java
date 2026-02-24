package com.example.project_datn_sd08_baseballcapsales.Model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostImageDto {

    @NotNull
    private Integer productColorID;

    @Size(max = 255)
    @Nationalized
    private String imageUrl;

    @ColumnDefault("0")
    private Boolean isMain;
}
