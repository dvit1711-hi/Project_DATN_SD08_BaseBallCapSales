package com.example.project_datn_sd08_baseballcapsales.Model.dto.ProductDto;

import com.example.project_datn_sd08_baseballcapsales.Model.entity.ProductColor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProductColorDto {
    private Integer productColorID;
    private String colorName;
    private Integer stockQuantity;
    private List<ImageDto> images;
//    private Integer colorId;

    public ProductColorDto(ProductColor pc) {
        this.productColorID = pc.getId();
        this.colorName = pc.getColorID().getColorName();
        this.stockQuantity = pc.getStockQuantity();
        this.images = pc.getImages()
                .stream()
                .map(ImageDto::new)
                .toList();
    }
}
