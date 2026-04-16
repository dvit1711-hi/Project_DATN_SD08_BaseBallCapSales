package com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StaffOrderDTO {
    private Integer staffId;
    private String staffName;
    private Long totalOrders;
}