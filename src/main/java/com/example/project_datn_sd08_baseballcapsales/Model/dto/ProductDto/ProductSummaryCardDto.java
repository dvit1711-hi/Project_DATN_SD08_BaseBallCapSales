package com.example.project_datn_sd08_baseballcapsales.Model.dto.ProductDto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ProductSummaryCardDto {

    private Integer productID;
    private String productName;
    private String status;

    private Integer brandID;
    private String brandName;

    private Integer materialID;
    private String materialName;

    private String displayImage;
    private BigDecimal displayPrice;

    private Boolean inStock;
    private Integer totalStock;

    private Integer defaultVariantId;

    private List<ColorDotDto> colors = new ArrayList<>();

    @Getter
    @Setter
    public static class ColorDotDto {
        private Integer colorID;
        private String colorName;
        private String colorCode;

        public ColorDotDto() {
        }

        public ColorDotDto(Integer colorID, String colorName, String colorCode) {
            this.colorID = colorID;
            this.colorName = colorName;
            this.colorCode = colorCode;
        }
    }
}