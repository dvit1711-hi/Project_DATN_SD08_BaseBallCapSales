package com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PosProductColorGetDto {
    private Integer productColorId;
    private String productName;
    private String colorName;
    private String sizeName;
    private BigDecimal price;
    private Integer stockQuantity;
    private String displayName;

    private String imageUrl;
}