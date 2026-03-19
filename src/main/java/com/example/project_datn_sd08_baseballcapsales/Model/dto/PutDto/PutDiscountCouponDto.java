package com.example.project_datn_sd08_baseballcapsales.Model.dto.PutDto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PutDiscountCouponDto {
    @Size(max = 50, message = "Mã không được vượt quá 50 ký tự")
    private String couponCode;

    @Size(max = 100, message = "Tên không được vượt quá 100 ký tự")
    private String name;

    private String discountType;

    private BigDecimal discountValue;

    private BigDecimal minOrderValue;

    private BigDecimal maxDiscountValue;

    private Integer quantity;

    private LocalDate startDate;

    private LocalDate endDate;

    private Boolean active;

    @Size(max = 500, message = "Mô tả không được vượt quá 500 ký tự")
    private String description;

    @Size(max = 20, message = "Status không được vượt quá 20 ký tự")
    private String status;
}