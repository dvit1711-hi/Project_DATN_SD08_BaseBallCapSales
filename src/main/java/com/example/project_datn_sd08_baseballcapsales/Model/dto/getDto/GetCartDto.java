package com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto;

import com.example.project_datn_sd08_baseballcapsales.Model.entity.Cart;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class GetCartDto {

    private Integer cartID;
    private Integer accountID;

    public GetCartDto(Cart cart) {
        this.cartID = cart.getId();
        if (cart.getAccount() != null) {
            this.accountID = cart.getAccount().getId();
        }
    }
}
