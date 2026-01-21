package com.example.project_datn_sd08_baseballcapsales.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "CartItems")
public class CartItem {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cartId", nullable = false)
    private com.example.project_datn_sd08_baseballcapsales.Model.Cart cart;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "productID", nullable = false)
    private com.example.project_datn_sd08_baseballcapsales.Model.Product productID;

    @NotNull
    @ColumnDefault("1")
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public com.example.project_datn_sd08_baseballcapsales.Model.Cart getCart() {
        return cart;
    }

    public void setCart(com.example.project_datn_sd08_baseballcapsales.Model.Cart cart) {
        this.cart = cart;
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

}