package com.example.project_datn_sd08_baseballcapsales.Model.dto.PutDto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PutProductDiscountDto {
    private String discountType;

    private BigDecimal discountValue;

    private BigDecimal maxDiscountValue;

    private Integer quantity;

    private LocalDate startDate;

    private LocalDate endDate;

    private Boolean active;

    @Size(max = 500, message = "Mô tả không được vượt quá 500 ký tự")
    private String description;

    @Size(max = 50, message = "Lý do không được vượt quá 50 ký tự")
    private String reason;
}
