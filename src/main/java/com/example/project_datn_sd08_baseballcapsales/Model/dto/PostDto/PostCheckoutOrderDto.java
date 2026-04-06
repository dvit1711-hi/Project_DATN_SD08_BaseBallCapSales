package com.example.project_datn_sd08_baseballcapsales.Model.dto.PostDto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PostCheckoutOrderDto {
    private String method; // CASH, BANKING, QR
    private BigDecimal cashReceived; // chỉ dùng cho tiền mặt
}