package com.example.project_datn_sd08_baseballcapsales.Model.dto.ProductDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductDetailDto {
    private Integer productID;
    private String productName;
    private String description;
    private String status;
    private Integer brandID;
    private String brandName;
    private Integer materialID;
    private String materialName;
    private List<ColorDetailDto> colors;
}