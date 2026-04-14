package com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto;

import com.example.project_datn_sd08_baseballcapsales.Model.entity.Product;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class GetProductDto {

    @Size(max = 200)
    @Nationalized
    private String productName;

    @Size(max = 500)
    @Nationalized
    private String description;

    @Size(max = 20)
    private String status;

    @Size(max = 100)
    @NotNull
    @Nationalized
    private String name;

    private Integer brandID;
    private Integer materialID;
    private String materialName;
    private Integer productID;

    private Integer variantCount;

    public GetProductDto(Product product) {
        this.productID = product.getId();
        this.productName = product.getProductName();
        this.description = product.getDescription();
        this.status = product.getStatus();

        if (product.getBrandID() != null) {
            this.brandID = product.getBrandID().getBrandID();
            this.name = product.getBrandID().getName();
        }

        if (product.getMaterialID() != null) {
            this.materialID = product.getMaterialID().getMaterialID();
            this.materialName = product.getMaterialID().getMaterialName();
        }
    }
}