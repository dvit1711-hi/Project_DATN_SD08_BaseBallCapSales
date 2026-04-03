package com.example.project_datn_sd08_baseballcapsales.Model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "DiscountCoupons")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class DiscountCoupon {

    @Id
    @Column(name = "couponID", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Size(max = 50)
    @Nationalized
    @Column(name = "couponCode", length = 50)
    private String couponCode;

    @Size(max = 100)
    @Nationalized
    @Column(name = "name", length = 100)
    private String name;

    @Size(max = 20)
    @Column(name = "discountType", length = 20)
    private String discountType; // percent hoặc fixed

    @Column(name = "discountValue", precision = 10, scale = 2)
    private BigDecimal discountValue;

    @Column(name = "minOrderValue", precision = 10, scale = 2)
    private BigDecimal minOrderValue;

    @Column(name = "maxDiscountValue", precision = 10, scale = 2)
    private BigDecimal maxDiscountValue;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "startDate")
    private LocalDate startDate;

    @Column(name = "endDate")
    private LocalDate endDate;

    @Column(name = "active")
    private Boolean active = true;

    @Size(max = 500)
    @Nationalized
    @Column(name = "description", length = 500)
    private String description;

    @Size(max = 20)
    @Nationalized
    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "createdAt", nullable = false, updatable = false)
    private LocalDate createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDate.now();
    }
}