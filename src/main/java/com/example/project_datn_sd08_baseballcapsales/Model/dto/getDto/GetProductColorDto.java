package com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto;

import com.example.project_datn_sd08_baseballcapsales.Model.entity.ProductColor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class GetProductColorDto {
    private Integer productColorID;
    private Integer productID;
    private String productName;
    private Integer colorID;
    private String colorName;
    private Integer sizeID;
    private String sizeName;
    private BigDecimal price;
    private Integer stockQuantity;

    public GetProductColorDto(ProductColor pc) {
        this.productColorID = pc.getId();
        this.productID = pc.getProductID().getId();
        this.productName = pc.getProductID().getProductName();
        this.colorID = pc.getColorID().getId();
        this.colorName = pc.getColorID().getColorName();
        this.sizeID = pc.getSizeID() != null ? pc.getSizeID().getSizeID() : null;
        this.sizeName = pc.getSizeID() != null ? pc.getSizeID().getSizeName() : null;
        this.price = pc.getPrice();
        this.stockQuantity = pc.getStockQuantity();
    }
}