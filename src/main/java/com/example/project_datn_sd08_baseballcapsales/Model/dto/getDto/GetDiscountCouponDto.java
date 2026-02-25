package com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto;

import com.example.project_datn_sd08_baseballcapsales.Model.entity.DiscountCoupon;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetDiscountCouponDto {
    @Size(max = 50)
    @Nationalized
    private String couponCode;

    private BigDecimal discountValue;

    private LocalDate expiryDate;

    @Size(max = 20)
    @Nationalized
    private String status;



    public GetDiscountCouponDto(DiscountCoupon discountCoupon) {
        this.couponCode = discountCoupon.getCouponCode();
        this.discountValue = discountCoupon.getDiscountValue();
        this.expiryDate = discountCoupon.getExpiryDate();
        this.status = discountCoupon.getStatus();
    }
}