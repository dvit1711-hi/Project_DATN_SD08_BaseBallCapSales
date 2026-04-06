package com.example.project_datn_sd08_baseballcapsales.payload.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class GhnShippingFeeRequest {
    private Integer toDistrictId;
    private String toWardCode;
    private Integer weight;
    private Integer length;
    private Integer width;
    private Integer height;
    private BigDecimal insuranceValue;
}
