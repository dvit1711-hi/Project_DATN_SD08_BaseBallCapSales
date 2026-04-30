package com.example.project_datn_sd08_baseballcapsales.Model.dto.payload.request;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostShippingReturnDto {
    private Integer orderDetailId;
    private Integer quantity;
    private String note;
}