package com.example.project_datn_sd08_baseballcapsales.Model.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostDiscountCouponDto {
    @Size(max = 50)
    @Nationalized
    private String couponCode;

    private BigDecimal discountValue;

    private LocalDate expiryDate;

    @Size(max = 20)
    @Nationalized
    private String status;
}