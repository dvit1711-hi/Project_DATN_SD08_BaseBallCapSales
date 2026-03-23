package com.example.project_datn_sd08_baseballcapsales.Model.dto.ProductDto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ColorDetailDto {
    private Integer productColorID;
    private String colorName;
    private String colorCode;
    private Integer stockQuantity;
    private List<String> images;
}
