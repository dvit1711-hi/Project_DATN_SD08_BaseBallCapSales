package com.example.project_datn_sd08_baseballcapsales.Model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "ProductColors")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class ProductColor {
    @Id
    @Column(name = "productColorID", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "productID", nullable = false)
    private Product product;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "colorID", nullable = false)
    private Color color;

    @Column(name = "stockQuantity")
    private Integer stockQuantity;

    @OneToMany(mappedBy = "productColor")
    private List<Image> images;
}