package com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto;

import com.example.project_datn_sd08_baseballcapsales.Model.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetOrderDto {

    private static final ZoneId VN_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");
    private static final DateTimeFormatter VN_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private Integer id;
    private String orderDate;
    private String status;
    private BigDecimal totalAmount;
    private String accountCode;
    private String couponCode;

    public GetOrderDto(Order order) {
        this.id = order.getId();

        this.orderDate = order.getOrderDate() != null
                ? order.getOrderDate().atZone(VN_ZONE).format(VN_FORMAT)
                : null;

        this.status = order.getStatus();
        this.totalAmount = order.getTotalAmount();

        this.accountCode = order.getAccountID() != null
                ? order.getAccountID().getEmail()
                : null;

        this.couponCode = order.getCouponID() != null
                ? order.getCouponID().getCouponCode()
                : null;
    }
}