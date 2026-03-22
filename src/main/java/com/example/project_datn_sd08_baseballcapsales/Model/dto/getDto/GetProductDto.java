package com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto;

import com.example.project_datn_sd08_baseballcapsales.Model.dto.ProductDto.ProductColorDto;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.Brand;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.Product;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;
import java.util.List;

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

    private BigDecimal price;

    @Size(max = 20)
    private String status;

    @Size(max = 100)
    @NotNull
    @Nationalized
    private String name;
    private Integer brandID; // thêm field brandID

    private Integer productID;

    public GetProductDto(Product product) {
        this.productID = product.getId();
        this.productName = product.getProductName();
        this.description = product.getDescription();
        this.price = product.getPrice();
        this.status = product.getStatus();
        if(product.getBrandID() != null){
            this.brandID = product.getBrandID().getBrand(); // ID của brand
            this.name = product.getBrandID().getName();
        }
    }
}
