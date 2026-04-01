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
@Table(name = "ProductDiscounts")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class ProductDiscount {
    @Id
    @Column(name = "discountID", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productColorID", nullable = false)
    private ProductColor productColor;

    @Size(max = 20)
    @Column(name = "discountType", length = 20)
    private String discountType; // percent or fixed

    @Column(name = "discountValue", precision = 10, scale = 2)
    private BigDecimal discountValue;

    @Column(name = "maxDiscountValue", precision = 10, scale = 2)
    private BigDecimal maxDiscountValue;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "quantityUsed")
    private Integer quantityUsed = 0;

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

    @Size(max = 50)
    @Nationalized
    @Column(name = "reason", length = 50)
    private String reason; // slow-selling, seasonal, etc.

    @Column(name = "createdAt", nullable = false, updatable = false)
    private LocalDate createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDate.now();
        if (quantityUsed == null) {
            quantityUsed = 0;
        }
    }
}
