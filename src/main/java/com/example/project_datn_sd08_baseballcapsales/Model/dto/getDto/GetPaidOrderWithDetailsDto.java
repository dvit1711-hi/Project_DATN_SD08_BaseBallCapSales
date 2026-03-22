package com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto;

import com.example.project_datn_sd08_baseballcapsales.Model.entity.Order;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.OrderDetail;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetPaidOrderWithDetailsDto {
    private Integer orderId;
    private Integer accountId;
    private String accountUsername;
    private Instant orderDate;
    private String orderStatus;
    private BigDecimal totalAmount;
    private String paymentStatus;
    private String paymentMethod;
    private List<OrderItemDetailsDto> items;

    public GetPaidOrderWithDetailsDto(Order order, String paymentStatus, String paymentMethod, List<OrderDetail> orderDetails) {
        this.orderId = order.getId();
        this.accountId = order.getAccountID().getId();
        this.accountUsername = order.getAccountID().getUsername();
        this.orderDate = order.getOrderDate();
        this.orderStatus = order.getStatus();
        this.totalAmount = order.getTotalAmount();
        this.paymentStatus = paymentStatus;
        this.paymentMethod = paymentMethod;
        this.items = new ArrayList<>();

        if (orderDetails != null) {
            for (OrderDetail detail : orderDetails) {
                items.add(new OrderItemDetailsDto(detail));
            }
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemDetailsDto {
        private Integer orderDetailId;
        private Integer productId;
        private String productName;
        private Integer quantity;
        private BigDecimal price;
        private String colorName;
        private String imageUrl;

        public OrderItemDetailsDto(OrderDetail detail) {
            this.orderDetailId = detail.getId();
            if (detail.getProductColorID() != null) {
                this.productId = detail.getProductColorID().getProductID().getId();
                this.productName = detail.getProductColorID().getProductID().getProductName();
                this.colorName = detail.getProductColorID().getColorID() != null ? detail.getProductColorID().getColorID().getColorName() : "Unknown";
                // Get first image if available
                if (!detail.getProductColorID().getImages().isEmpty()) {
                    this.imageUrl = detail.getProductColorID().getImages().get(0).getImageUrl();
                }
            }
            this.quantity = detail.getQuantity();
            this.price = detail.getPrice();
        }
    }
}