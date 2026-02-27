package com.example.project_datn_sd08_baseballcapsales.Model.dto.ProductDto;

import com.example.project_datn_sd08_baseballcapsales.Model.entity.Image;

public class ImageDto {
    private String imageUrl;
    private Boolean isMain;

    public ImageDto(Image image) {
        this.imageUrl = image.getImageUrl();
        this.isMain = image.getIsMain();
    }
}
