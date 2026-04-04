package com.example.project_datn_sd08_baseballcapsales.Model.dto.PostDto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PostMaterialDto {
    private String materialName;
    @Size(max = 20)
    private String status;
}
