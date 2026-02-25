package com.example.project_datn_sd08_baseballcapsales.Model.dto.PutDto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.Nationalized;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class PutBrandDto {

    @Size(max = 100)
    @NotNull
    @Nationalized
    private String name;
}
