package com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class PosOrderGetDto {
    private Integer orderId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime orderDate;

    private String orderType;
    private String status;

    private Integer customerId;
    private String customerName;
    private String customerPhone;

    private Integer employeeId;
    private String employeeName;

    private String note;
    private String shippingAddress;
    private String couponCode;

    private BigDecimal subtotal;
    private BigDecimal discountAmount;
    private BigDecimal totalAmount;

    private String paymentMethod;
    private String paymentStatus;

    private List<PosOrderItemGetDto> items;
}