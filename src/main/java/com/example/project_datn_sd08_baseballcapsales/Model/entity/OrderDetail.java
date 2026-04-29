package com.example.project_datn_sd08_baseballcapsales.Model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "OrderDetails")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class OrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orderDetailsID")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orderID", nullable = false)
    private Order orderID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productColorID", nullable = false)
    private ProductColor productColorID;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "price", nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(name = "returnedQuantity", nullable = false)
    private Integer returnedQuantity = 0;

    @Column(name = "shippingReturnedQuantity", nullable = false)
    private Integer shippingReturnedQuantity = 0;

    @Column(name = "completedReturnedQuantity", nullable = false)
    private Integer completedReturnedQuantity = 0;

    @PrePersist
    public void prePersist() {
        if (this.returnedQuantity == null) {
            this.returnedQuantity = 0;
        }

        if (this.shippingReturnedQuantity == null) {
            this.shippingReturnedQuantity = 0;
        }

        if (this.completedReturnedQuantity == null) {
            this.completedReturnedQuantity = 0;
        }
    }


}