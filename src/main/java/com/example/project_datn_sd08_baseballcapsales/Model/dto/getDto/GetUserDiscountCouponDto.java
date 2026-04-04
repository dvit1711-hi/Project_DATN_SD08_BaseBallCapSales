package com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto;

import com.example.project_datn_sd08_baseballcapsales.Model.entity.UserDiscountCoupon;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetUserDiscountCouponDto {
    private Integer id;
    private Integer accountId;
    private Integer couponId;
    private Instant claimedDate;
    private Instant usedDate;
    private String status;
    private Instant createdAt;
    
    // Include coupon details
    private GetDiscountCouponDto discountCoupon;

    public GetUserDiscountCouponDto(UserDiscountCoupon userDiscountCoupon) {
        this.id = userDiscountCoupon.getId();
        this.accountId = userDiscountCoupon.getAccountId();
        this.couponId = userDiscountCoupon.getCouponId();
        this.claimedDate = userDiscountCoupon.getClaimedDate();
        this.usedDate = userDiscountCoupon.getUsedDate();
        this.status = userDiscountCoupon.getStatus();
        this.createdAt = userDiscountCoupon.getCreatedAt();
        
        if (userDiscountCoupon.getDiscountCoupon() != null) {
            this.discountCoupon = new GetDiscountCouponDto(userDiscountCoupon.getDiscountCoupon());
        }
    }
}
