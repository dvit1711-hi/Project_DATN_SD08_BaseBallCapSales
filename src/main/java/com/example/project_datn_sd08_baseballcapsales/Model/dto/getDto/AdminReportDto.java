package com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AdminReportDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StaffOptionDto {
        private Integer employeeId;
        private String username;
        private String email;
        private String displayName;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StaffTodayItemDto {
        private Integer employeeId;
        private String employeeName;
        private String email;
        private Long totalOrdersToday;
        private Long totalProductsToday;
        private BigDecimal totalRevenueToday;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StaffTodayDashboardDto {
        private Integer employeeId;
        private String employeeName;
        private Long totalOrdersToday;
        private Long totalProductsToday;
        private BigDecimal totalRevenueToday;
        private List<CustomerPurchaseHistoryDto> recentOrders = new ArrayList<>();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerSearchDto {
        private Integer customerId;
        private String customerName;
        private String customerPhone;
        private String customerType;
        private Long totalOrders;
        private BigDecimal totalSpent;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerPurchaseHistoryDto {
        private Integer orderId;
        private String trackingCode;
        private LocalDateTime orderDate;
        private String orderType;
        private String orderStatus;

        private Integer customerId;
        private String customerName;
        private String customerPhone;

        private Integer employeeId;
        private String employeeName;

        private BigDecimal totalAmount;
        private String paymentMethod;
        private String paymentStatus;
        private Integer totalItems;

        private List<OrderItemLiteDto> items = new ArrayList<>();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemLiteDto {
        private Integer orderDetailId;
        private Integer productColorId;
        private String productName;
        private String colorName;
        private String sizeName;
        private Integer quantity;
        private BigDecimal price;
        private BigDecimal lineTotal;
    }
}