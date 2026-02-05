package com.example.project_datn_sd08_baseballcapsales.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "ProductColors")
public class ProductColor {
    @Id
    @Column(name = "productColorID", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "productID", nullable = false)
    private com.example.project_datn_sd08_baseballcapsales.Model.Product productID;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "colorID", nullable = false)
    private Color colorID;

    @Column(name = "stockQuantity")
    private Integer stockQuantity;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public com.example.project_datn_sd08_baseballcapsales.Model.Product getProductID() {
        return productID;
    }

    public void setProductID(com.example.project_datn_sd08_baseballcapsales.Model.Product productID) {
        this.productID = productID;
    }

    public Color getColorID() {
        return colorID;
    }

    public void setColorID(Color colorID) {
        this.colorID = colorID;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

}