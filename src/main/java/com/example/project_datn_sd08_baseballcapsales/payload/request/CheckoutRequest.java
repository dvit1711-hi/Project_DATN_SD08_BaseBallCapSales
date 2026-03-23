package com.example.project_datn_sd08_baseballcapsales.payload.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class CheckoutRequest {
    private Integer accountId;
    private String method;
    private List<Integer> cartItemIds;
    private String couponCode;
}
