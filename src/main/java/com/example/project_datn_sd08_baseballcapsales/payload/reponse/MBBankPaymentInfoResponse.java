package com.example.project_datn_sd08_baseballcapsales.payload.reponse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MBBankPaymentInfoResponse {
    private Integer orderId;
    private BigDecimal amount;
    private String bankCode;
    private String bankName;
    private String accountNumber;
    private String accountName;
    private String transferContent;
    private String qrUrl;
}