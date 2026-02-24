package com.example.project_datn_sd08_baseballcapsales.Model.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class PutCartItemDto {

    private Integer cartID;
    private Integer productID;
    private Integer quantity;
}
