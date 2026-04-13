package com.example.project_datn_sd08_baseballcapsales.Model.dto.ProductDto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class ColorDetailDto {
    private Integer productColorID;
    private Integer colorID;
    private String colorName;
    private String colorCode;
    private Integer stockQuantity;
    private BigDecimal price;
    private Integer sizeID;
    private String sizeName;
    private String status;
    private List<ImageDto> images;
}