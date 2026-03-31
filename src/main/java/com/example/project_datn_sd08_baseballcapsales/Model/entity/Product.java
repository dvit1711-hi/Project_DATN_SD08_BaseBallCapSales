package com.example.project_datn_sd08_baseballcapsales.Model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.util.List;

@Entity
@Table(name = "Products")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Product {

    @Id
    @Column(name = "productID", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Size(max = 200)
    @Nationalized
    @Column(name = "productName", length = 200)
    private String productName;

    @Size(max = 500)
    @Nationalized
    @Column(name = "description", length = 500)
    private String description;

    @Size(max = 20)
    @Column(name = "status", length = 20)
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brandID")
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
    private Brand brandID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "materialID")
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
    private Material materialID;

    @OneToMany(mappedBy = "productID")
    private List<ProductColor> productColors;

    public Product(String productName, Brand brandID, String description, String status, Material materialID) {
        this.productName = productName;
        this.brandID = brandID;
        this.description = description;
        this.status = status;
        this.materialID = materialID;
    }

    public Product(Integer id, String productName, String description, String status, Brand brandID, Material materialID) {
        this.id = id;
        this.productName = productName;
        this.description = description;
        this.status = status;
        this.brandID = brandID;
        this.materialID = materialID;
    }
}