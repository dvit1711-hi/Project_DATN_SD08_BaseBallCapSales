package com.example.project_datn_sd08_baseballcapsales.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "DiscountCoupons")
public class DiscountCoupon {
    @Id
    @Column(name = "couponID", nullable = false)
    private Integer id;

    @Size(max = 50)
    @Nationalized
    @Column(name = "couponCode", length = 50)
    private String couponCode;

    @Column(name = "discountValue", precision = 10, scale = 2)
    private BigDecimal discountValue;

    @Column(name = "expiryDate")
    private LocalDate expiryDate;

    @Size(max = 20)
    @Nationalized
    @Column(name = "Status", length = 20)
    private String status;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "orderDetailsID", nullable = false)
    private com.example.project_datn_sd08_baseballcapsales.Model.OrderDetail orderDetailsID;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public BigDecimal getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(BigDecimal discountValue) {
        this.discountValue = discountValue;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public com.example.project_datn_sd08_baseballcapsales.Model.OrderDetail getOrderDetailsID() {
        return orderDetailsID;
    }

    public void setOrderDetailsID(com.example.project_datn_sd08_baseballcapsales.Model.OrderDetail orderDetailsID) {
        this.orderDetailsID = orderDetailsID;
    }

}