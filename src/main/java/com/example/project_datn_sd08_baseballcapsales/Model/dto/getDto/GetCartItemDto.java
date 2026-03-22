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
    private Integer productID;
    private Integer quantity;

    public GetCartItemDto(CartItem item) {
        this.cartItemID = item.getId();
        if (item.getCart() != null) {
            this.cartID = item.getCart().getId();
        }
        if (item.getProduct() != null) {
            this.productID = item.getProduct().getId();
        }
        this.quantity = item.getQuantity();
    }
}
