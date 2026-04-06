package com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PosPromotionGetDto {
    private Integer couponId;
    private String couponCode;
    private String name;
    private String description;

    private String discountType;
    private BigDecimal discountValue;
    private BigDecimal minOrderValue;
    private BigDecimal maxDiscountValue;

    private boolean eligible;
    private boolean applied;

    private BigDecimal estimatedDiscount;
    private BigDecimal missingAmount;
}