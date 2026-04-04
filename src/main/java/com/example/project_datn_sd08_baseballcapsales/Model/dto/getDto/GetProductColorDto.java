package com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto;

import com.example.project_datn_sd08_baseballcapsales.Model.entity.ProductColor;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class GetProductColorDto {
    private Integer id;
    private Integer productID;
    private String productName;
    private Integer colorID;
    private String colorName;
    private String colorCode;
    private Integer sizeID;
    private String sizeName;
    private BigDecimal price;
    private Integer stockQuantity;
    private String mainImage;
    @Size(max = 20)
    private String status;

    public GetProductColorDto(ProductColor pc) {
        this.id = pc.getId();
        this.productID = pc.getProductID().getId();
        this.productName = pc.getProductID().getProductName();
        this.colorID = pc.getColorID().getId();
        this.colorName = pc.getColorID().getColorName();
        this.colorCode = pc.getColorID().getColorCode();
        this.sizeID = pc.getSizeID() != null ? pc.getSizeID().getSizeID() : null;
        this.sizeName = pc.getSizeID() != null ? pc.getSizeID().getSizeName() : null;
        this.price = pc.getPrice();
        this.status =pc.getStatus();
        this.stockQuantity = pc.getStockQuantity();
        // Get first image from images list if available
        this.mainImage = (pc.getImages() != null && !pc.getImages().isEmpty()) 
            ? pc.getImages().get(0).getImageUrl() 
            : null;
    }
}