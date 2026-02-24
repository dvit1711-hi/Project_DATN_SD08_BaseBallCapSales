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

    @Column(name = "discountValue", precision = 10, scale = 2)
    private BigDecimal discountValue;

    @Column(name = "expiryDate")
    private LocalDate expiryDate;

    @Size(max = 20)
    @Nationalized
    @Column(name = "status", length = 20)
    private String status;

}