package com.example.project_datn_sd08_baseballcapsales.Model.dto.PostDto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostProductDiscountDto {
    @NotNull(message = "ID biến thể màu không được trống")
    private Integer productColorId;

    @NotBlank(message = "Loại giảm giá không được trống")
    private String discountType; // percent or fixed

    @NotNull(message = "Giá trị giảm không được trống")
    @DecimalMin(value = "0.01", message = "Giá trị phải lớn hơn 0")
    private BigDecimal discountValue;

    @DecimalMin(value = "0", message = "Không được âm")
    private BigDecimal maxDiscountValue;

    @NotNull(message = "Số lượng không được trống")
    @Min(value = 1, message = "Số lượng phải >= 1")
    private Integer quantity;

    @NotNull(message = "Ngày bắt đầu không được trống")
    private LocalDate startDate;

    @NotNull(message = "Ngày kết thúc không được trống")
    private LocalDate endDate;

    private Boolean active = true;

    @Size(max = 500, message = "Mô tả không được vượt quá 500 ký tự")
    private String description;

    @Size(max = 50, message = "Lý do không được vượt quá 50 ký tự")
    private String reason; // slow-selling, seasonal, etc.
}
