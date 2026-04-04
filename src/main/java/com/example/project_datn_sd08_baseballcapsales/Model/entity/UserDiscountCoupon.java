package com.example.project_datn_sd08_baseballcapsales.Model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "UserDiscountCoupons")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class UserDiscountCoupon {

    @Id
    @Column(name = "userCouponID", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "accountID", nullable = false)
    private Integer accountId;

    @Column(name = "couponID", nullable = false)
    private Integer couponId;

    @Column(name = "claimedDate", nullable = false, updatable = false)
    @CreationTimestamp
    private Instant claimedDate;

    @Column(name = "usedDate")
    private Instant usedDate;

    @Column(name = "status", length = 20, nullable = false)
    private String status; // claimed, used, expired

    @Column(name = "createdAt", nullable = false, updatable = false)
    @CreationTimestamp
    private Instant createdAt;

    // Foreign key relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accountID", referencedColumnName = "accountID", insertable = false, updatable = false)
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "couponID", referencedColumnName = "couponID", insertable = false, updatable = false)
    private DiscountCoupon discountCoupon;
}
