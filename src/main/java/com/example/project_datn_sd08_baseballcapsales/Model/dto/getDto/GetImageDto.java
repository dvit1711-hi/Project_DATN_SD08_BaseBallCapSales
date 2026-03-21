package com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto;

import com.example.project_datn_sd08_baseballcapsales.Model.entity.Image;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetImageDto {

    @Size(max = 255)
    @Nationalized
    private String imageUrl;

    @ColumnDefault("0")
    private Boolean isMain;

    @Size(max = 200)
    @Nationalized
    private String productName;

    public GetImageDto(Image img) {
        this.imageUrl = img.getImageUrl();
        this.isMain = img.getIsMain();
        if (img.getProductColor() != null
                && img.getProductColor().getProduct() != null) {

            this.productName = img.getProductColor()
                    .getProduct()
                    .getProductName();
        }
    }
}
