package com.example.project_datn_sd08_baseballcapsales.Model.dto.ProductDto;

import com.example.project_datn_sd08_baseballcapsales.Model.entity.Image;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.ProductColor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductCardDto {

    private Integer productID;
    private Integer productColorID;
    private String productName;
    private BigDecimal price;
    private String colorName;
    private String colorCode;
    private String mainImage;

    public ProductCardDto(ProductColor pc) {

        this.productID = pc.getProductID().getId();
        this.productColorID = pc.getId();
        this.productName = pc.getProductID().getProductName();
        this.price = pc.getProductID().getPrice();

        this.colorName = pc.getColorID().getColorName();
        this.colorCode = pc.getColorID().getColorCode();

        this.mainImage = pc.getImages()
                .stream()
                .filter(Image::getIsMain)
                .findFirst()
                .map(Image::getImageUrl)
                .orElse(null);
    }
}