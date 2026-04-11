package com.example.project_datn_sd08_baseballcapsales.Model.dto.ProductDto;

import com.example.project_datn_sd08_baseballcapsales.Model.entity.Image;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImageDto {
    private Integer imageID;
    private String imageUrl;
    private Boolean isMain;

    public ImageDto(Image image) {
        this.imageID = image.getId();
        this.imageUrl = image.getImageUrl();
        this.isMain = image.getIsMain();
    }
}
