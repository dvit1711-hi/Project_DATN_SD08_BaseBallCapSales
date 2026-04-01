package com.example.project_datn_sd08_baseballcapsales.Model.dto.ProductDto;

import com.example.project_datn_sd08_baseballcapsales.Model.entity.ProductColor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class ProductColorDto {

    private Integer productColorID;
    private String colorName;
    private String colorCode;
    private Integer sizeID;
    private String sizeName;
    private BigDecimal price;
    private Integer stockQuantity;
    private List<ImageDto> images;

    public ProductColorDto(ProductColor pc) {
        this.productColorID = pc.getId();
        this.colorName = pc.getColorID().getColorName();
        this.colorCode = pc.getColorID().getColorCode();
        this.sizeID = pc.getSizeID() != null ? pc.getSizeID().getSizeID() : null;
        this.sizeName = pc.getSizeID() != null ? pc.getSizeID().getSizeName() : null;
        this.price = pc.getPrice();
        this.stockQuantity = pc.getStockQuantity();
        this.images = pc.getImages()
                .stream()
                .map(ImageDto::new)
                .toList();
    }
}