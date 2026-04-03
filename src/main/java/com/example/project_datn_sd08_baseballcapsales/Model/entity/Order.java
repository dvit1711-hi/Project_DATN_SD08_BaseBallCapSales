package com.example.project_datn_sd08_baseballcapsales.Model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accountID")
    private Account accountID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "couponID")
    private DiscountCoupon couponID;

    @Column(name = "orderDate", updatable = false, nullable = false)
    private LocalDateTime orderDate;

    @Size(max = 50)
    @Nationalized
    @Column(name = "status", length = 50)
    private String status;

    @Size(max = 20)
    @Nationalized
    @Column(name = "orderType", length = 20)
    private String orderType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employeeID")
    private Account employeeID;

    @Size(max = 100)
    @Nationalized
    @Column(name = "customerName", length = 100)
    private String customerName;

    @Size(max = 20)
    @Nationalized
    @Column(name = "customerPhone", length = 20)
    private String customerPhone;

    @Size(max = 500)
    @Nationalized
    @Column(name = "note", length = 500)
    private String note;

    @Size(max = 500)
    @Nationalized
    @Column(name = "shippingAddress", length = 500)
    private String shippingAddress;

    @Column(name = "totalAmount", precision = 18, scale = 2)
    private BigDecimal totalAmount;

    @PrePersist
    public void prePersist() {
        if (this.orderDate == null) {
            this.orderDate = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        }
    }
}