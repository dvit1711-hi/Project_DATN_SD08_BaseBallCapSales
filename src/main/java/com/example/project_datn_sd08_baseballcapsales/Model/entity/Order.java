package com.example.project_datn_sd08_baseballcapsales.Model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "Orders")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Order {
    @Id
    @Column(name = "orderID", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "accountID", nullable = false)
    private Account accountID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "couponID")
    private DiscountCoupon couponID;

    @ColumnDefault("getdate()")
    @Column(name = "orderDate")
    private Instant orderDate;

    @Size(max = 50)
    @Nationalized
    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "totalAmount", precision = 18, scale = 2)
    private BigDecimal totalAmount;


}