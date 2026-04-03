package com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PosOrderItemGetDto {
    private Integer orderDetailId;
    private Integer productColorId;
    private String productName;
    private String colorName;
    private String sizeName;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal lineTotal;
    private Integer stockQuantity;
}