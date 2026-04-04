package com.example.project_datn_sd08_baseballcapsales.Model.dto.PostDto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostUserDiscountCouponDto {
    @NotNull(message = "Mã coupon không được để trống")
    private Integer couponId;

    @NotNull(message = "Mã tài khoản không được để trống")
    private Integer accountId;
}
