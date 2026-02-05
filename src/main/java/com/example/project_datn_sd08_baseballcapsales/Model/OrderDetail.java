package com.example.project_datn_sd08_baseballcapsales.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Entity
@Table(name = "OrderDetails")
public class OrderDetail {
    @Id
    @Column(name = "orderDetailsID", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "orderID", nullable = false)
    private com.example.project_datn_sd08_baseballcapsales.Model.Order orderID;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "productID", nullable = false)
    private com.example.project_datn_sd08_baseballcapsales.Model.Product productID;

    @NotNull
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @NotNull
    @Column(name = "price", nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public com.example.project_datn_sd08_baseballcapsales.Model.Order getOrderID() {
        return orderID;
    }

    public void setOrderID(com.example.project_datn_sd08_baseballcapsales.Model.Order orderID) {
        this.orderID = orderID;
    }

    public com.example.project_datn_sd08_baseballcapsales.Model.Product getProductID() {
        return productID;
    }

    public void setProductID(com.example.project_datn_sd08_baseballcapsales.Model.Product productID) {
        this.productID = productID;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

}