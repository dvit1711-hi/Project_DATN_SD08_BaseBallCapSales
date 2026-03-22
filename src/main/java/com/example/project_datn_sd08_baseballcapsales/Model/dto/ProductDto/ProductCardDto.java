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
    private String status;

    public ProductCardDto(ProductColor pc) {

        this.productID = pc.getProduct().getId();
        this.productColorID = pc.getId();
        this.productName = pc.getProduct().getProductName();
        this.price = pc.getProduct().getPrice();
        this.status = pc.getProduct().getStatus();

        this.colorName = pc.getColor().getColorName();
        this.colorCode = pc.getColor().getColorCode();

        this.mainImage = pc.getImages()
                .stream()
                .filter(Image::getIsMain)
                .findFirst()
                .map(Image::getImageUrl)
                .orElse(null);
    }
}