package com.example.project_datn_sd08_baseballcapsales.Model.entity;

import com.example.project_datn_sd08_baseballcapsales.Model.enums.OrderStatus;
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
import java.util.Random;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50)
    private OrderStatus status;

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

    // ➕ FIELD MÃ VẬN ĐƠN
    @Column(name = "trackingCode", length = 30, unique = true)
    private String trackingCode;

    // ================== AUTO GENERATE ===================
    @PrePersist
    public void prePersist() {

        if (this.orderDate == null) {
            this.orderDate = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        }

        if (this.orderType == null || this.orderType.trim().isEmpty()) {
            this.orderType = "ONLINE";
        }

        // Sinh mã vận đơn nếu chưa có
        if (this.trackingCode == null || this.trackingCode.isEmpty()) {
            this.trackingCode = generateTrackingCode();
        }
    }

    private String generateTrackingCode() {
        // Ngày: YYYYMMDD
        String date = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"))
                .toLocalDate()
                .toString()
                .replace("-", ""); // 20240409

        // Sinh 3 ký tự chữ ngẫu nhiên
        Random random = new Random();
        StringBuilder letters = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            letters.append((char) ('A' + random.nextInt(26)));
        }

        return "DTVD" + date + letters;
    }
}