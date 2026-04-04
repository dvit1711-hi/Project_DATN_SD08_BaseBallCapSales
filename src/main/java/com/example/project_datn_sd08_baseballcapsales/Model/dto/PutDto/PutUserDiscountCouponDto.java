package com.example.project_datn_sd08_baseballcapsales.Model.dto.PutDto;

import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PutUserDiscountCouponDto {
    @Size(max = 20, message = "Status không được vượt quá 20 ký tự")
    private String status;
}
