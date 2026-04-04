package com.example.project_datn_sd08_baseballcapsales.Model.dto.PutDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PutOfflineOrderInfoDto {
    private Integer accountId;
    private String customerName;
    private String customerPhone;
    private String note;
    private String shippingAddress;
}