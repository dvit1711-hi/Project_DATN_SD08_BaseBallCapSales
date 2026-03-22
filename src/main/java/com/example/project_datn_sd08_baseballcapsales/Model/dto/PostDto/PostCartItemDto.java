package com.example.project_datn_sd08_baseballcapsales.Model.dto.PostDto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class PostCartItemDto {

    private Integer cartID;
    private Integer productID;
    private Integer quantity;
}