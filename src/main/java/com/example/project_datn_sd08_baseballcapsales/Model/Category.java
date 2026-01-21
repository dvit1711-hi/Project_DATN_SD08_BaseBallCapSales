package com.example.project_datn_sd08_baseballcapsales.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.Nationalized;

@Entity
@Table(name = "Categories")
public class Category {
    @Id
    @Column(name = "categorieID", nullable = false)
    private Integer id;

    @Size(max = 100)
    @NotNull
    @Nationalized
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "productID", nullable = false)
    private com.example.project_datn_sd08_baseballcapsales.Model.Product productID;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public com.example.project_datn_sd08_baseballcapsales.Model.Product getProductID() {
        return productID;
    }

    public void setProductID(com.example.project_datn_sd08_baseballcapsales.Model.Product productID) {
        this.productID = productID;
    }

}