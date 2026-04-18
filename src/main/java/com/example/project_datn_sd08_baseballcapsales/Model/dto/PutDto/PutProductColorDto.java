package com.example.project_datn_sd08_baseballcapsales.Model.dto.PutDto;

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
public class PutProductColorDto {

    private Integer colorID;
    private Integer sizeID;
    private BigDecimal price;
    private Integer stockQuantity;
    private Boolean isRepresentative;
    @Size(max = 20)
    private String status;
}