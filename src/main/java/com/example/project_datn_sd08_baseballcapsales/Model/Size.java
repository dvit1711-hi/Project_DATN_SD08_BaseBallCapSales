package com.example.project_datn_sd08_baseballcapsales.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.Nationalized;

@Entity
@Table(name = "Sizes")
public class Size {
    @Id
    @Column(name = "sizeID", nullable = false)
    private Integer id;

    @jakarta.validation.constraints.Size(max = 20)
    @NotNull
    @Nationalized
    @Column(name = "name", nullable = false, length = 20)
    private String name;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "productID", nullable = false)
    private Product productID;

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

    public Product getProductID() {
        return productID;
    }

    public void setProductID(Product productID) {
        this.productID = productID;
    }

}