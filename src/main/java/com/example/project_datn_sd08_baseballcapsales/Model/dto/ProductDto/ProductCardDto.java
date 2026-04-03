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
    private String status;

    private Integer brandID;
    private String brandName;

    private Integer materialID;
    private String materialName;

    private Integer colorID;
    private String colorName;
    private String colorCode;

    private Integer sizeID;
    private String sizeName;

    private String mainImage;
    private Integer stockQuantity;

    public ProductCardDto(ProductColor pc) {
        this.productID = pc.getProductID().getId();
        this.productColorID = pc.getId();

        this.productName = pc.getProductID().getProductName();
        this.price = pc.getPrice();
        this.status = pc.getProductID().getStatus();
        this.stockQuantity = pc.getStockQuantity();

        if (pc.getProductID().getBrandID() != null) {
            this.brandID = pc.getProductID().getBrandID().getBrandID();
            this.brandName = pc.getProductID().getBrandID().getName();
        }

        if (pc.getProductID().getMaterialID() != null) {
            this.materialID = pc.getProductID().getMaterialID().getMaterialID();
            this.materialName = pc.getProductID().getMaterialID().getMaterialName();
        }

        if (pc.getColorID() != null) {
            this.colorID = pc.getColorID().getId(); // nếu entity Color dùng getColorID() thì đổi lại
            this.colorName = pc.getColorID().getColorName();
            this.colorCode = pc.getColorID().getColorCode();
        }

        if (pc.getSizeID() != null) {
            this.sizeID = pc.getSizeID().getSizeID(); // nếu SizeEntity dùng getId() thì đổi lại
            this.sizeName = pc.getSizeID().getSizeName();
        }

        this.mainImage = pc.getImages()
                .stream()
                .filter(Image::getIsMain)
                .findFirst()
                .map(Image::getImageUrl)
                .orElse(null);
    }
}