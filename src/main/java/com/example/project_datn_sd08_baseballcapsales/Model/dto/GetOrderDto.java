package com.example.project_datn_sd08_baseballcapsales.Model.dto;

import com.example.project_datn_sd08_baseballcapsales.Model.entity.Account;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.DiscountCoupon;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.Order;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetOrderDto {

    @Id
    private Integer id;
//    @NotNull
//    @ManyToOne(fetch = FetchType.LAZY, optional = false)
//    private Account accountID;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    private DiscountCoupon couponID;

    @ColumnDefault("getdate()")
    private Instant orderDate;

    @Size(max = 50)
    @Nationalized
    private String status;

    private BigDecimal totalAmount;

    @Size(max = 50)
    @NotNull
    @Nationalized
    private String accountCode;

    @Size(max = 50)
    @Nationalized
    private String couponCode;

    public GetOrderDto(Order order) {
        this.id = order.getId();
        this.orderDate = order.getOrderDate();
        this.status = order.getStatus();
        this.totalAmount = order.getTotalAmount();
        if (order.getAccountID() != null && order.getAccountID().getEmail() != null) {
            this.accountCode = order.getAccountID().getEmail();
        }
        if (order.getCouponID() != null && order.getCouponID().getCouponCode() != null) {
            this.couponCode = order.getCouponID().getCouponCode();
        }
    }
}
