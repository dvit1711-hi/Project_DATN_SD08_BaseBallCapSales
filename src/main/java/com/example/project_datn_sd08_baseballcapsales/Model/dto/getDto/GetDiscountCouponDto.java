package com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto;

import com.example.project_datn_sd08_baseballcapsales.Model.entity.DiscountCoupon;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetDiscountCouponDto {
    private Integer id;

    @Size(max = 50)
    private String couponCode;

    @Size(max = 100)
    private String name;

    @Size(max = 20)
    private String discountType;

    private BigDecimal discountValue;

    private BigDecimal minOrderValue;

    private BigDecimal maxDiscountValue;

    private Integer quantity;

    private LocalDate startDate;

    private LocalDate endDate;

    private Boolean active;

    @Size(max = 255)
    private String description;

    @Size(max = 20)
    private String status;

    private LocalDate createdAt;

    public GetDiscountCouponDto(DiscountCoupon discountCoupon) {
        this.id = discountCoupon.getId();
        this.couponCode = discountCoupon.getCouponCode();
        this.name = discountCoupon.getName();
        this.discountType = discountCoupon.getDiscountType();
        this.discountValue = discountCoupon.getDiscountValue();
        this.minOrderValue = discountCoupon.getMinOrderValue();
        this.maxDiscountValue = discountCoupon.getMaxDiscountValue();
        this.quantity = discountCoupon.getQuantity();
        this.startDate = discountCoupon.getStartDate();
        this.endDate = discountCoupon.getEndDate();
        this.active = discountCoupon.getActive();
        this.description = discountCoupon.getDescription();
        this.status = discountCoupon.getStatus();
        this.createdAt = discountCoupon.getCreatedAt();
    }
}