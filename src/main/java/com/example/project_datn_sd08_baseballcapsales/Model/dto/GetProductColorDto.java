package com.example.project_datn_sd08_baseballcapsales.Model.dto;

import com.example.project_datn_sd08_baseballcapsales.Model.entity.Color;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.Product;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.ProductColor;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.Nationalized;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetProductColorDto {

    @Id
    private Integer id;

//    @NotNull
//    @ManyToOne(fetch = FetchType.LAZY, optional = false)
//    private Product productID;
//
//    @NotNull
//    @ManyToOne(fetch = FetchType.LAZY, optional = false)
//    private Color colorID;

    private Integer stockQuantity;

    @Size(max = 200)
    @Nationalized
    private String productName;

    @Size(max = 50)
    @Nationalized
    private String colorName;

    public GetProductColorDto(ProductColor pc) {
        this.id = pc.getId();
        this.stockQuantity = pc.getStockQuantity();

        if (pc.getProductID() != null) {
            this.productName = pc.getProductID().getProductName();
        }

        if (pc.getColorID() != null) {
            this.colorName = pc.getColorID().getColorName();
        }
    }
}
