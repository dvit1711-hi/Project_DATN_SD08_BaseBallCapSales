package com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto;

import com.example.project_datn_sd08_baseballcapsales.Model.entity.CartItem;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class GetCartItemDto {

    private Integer cartItemID;
    private Integer cartID;
    private Integer productColorID;
    private String productName;
    private String colorName;
    private String colorCode;
    private Integer sizeID;
    private String sizeName;
    private String mainImage;
    private Integer quantity;
    private Long price;
    private Integer stockQuantity;

    public GetCartItemDto(CartItem item) {
        this.cartItemID = item.getId();
        if (item.getCartID() != null) {
            this.cartID = item.getCartID().getId();
        }
        if (item.getProductColorID() != null) {
            this.productColorID = item.getProductColorID().getId();
            this.price = item.getProductColorID().getPrice() == null
                    ? 0L
                    : item.getProductColorID().getPrice().longValue();
            this.stockQuantity = item.getProductColorID().getStockQuantity();
            this.productName = item.getProductColorID().getProductID().getProductName();
            if (item.getProductColorID().getImages() != null && !item.getProductColorID().getImages().isEmpty()) {
                this.mainImage = item.getProductColorID().getImages()
                        .stream()
                        .filter(img -> Boolean.TRUE.equals(img.getIsMain()))
                        .findFirst()
                        .orElse(item.getProductColorID().getImages().get(0))
                        .getImageUrl();
            }
            if (item.getProductColorID().getColorID() != null) {
                this.colorName = item.getProductColorID().getColorID().getColorName();
                this.colorCode = item.getProductColorID().getColorID().getColorCode();
            }
            if (item.getProductColorID().getSizeID() != null) {
                this.sizeID = item.getProductColorID().getSizeID().getSizeID();
                this.sizeName = item.getProductColorID().getSizeID().getSizeName();
            }
        }
        this.quantity = item.getQuantity();
    }
}
