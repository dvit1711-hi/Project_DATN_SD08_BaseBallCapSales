package com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsDto {
    private Long totalOrders;
    private BigDecimal totalRevenue;
    private Long totalCustomers;
    private Long totalProducts;

    private Long totalShipment;
    private Long totalDelivery;
    private Long pending;
    private Double deliveryRate;

    private Map<String, Long> ordersByStatus;
    private Map<String, Long> ordersByDay;
    private Map<String, Long> ordersByMonth;
    private BigDecimal averageOrderValue;

    private List<ProductSalesDto> topProducts;
    private List<BrandSalesDto> topBrands;
    private List<ColorStockDto> inventoryByColor;
    private List<ProductStockDto> lowStockProducts;

    private CustomerStatsDto topCustomerByOrders;
    private CustomerStatsDto topCustomerBySpending;
    private Map<String, Long> accountStatusCounts;

    private Map<String, Long> paymentMethodCounts;
    private Map<String, Long> paymentStatusCounts;
    private BigDecimal totalPaidAmount;

    private Double averageRating;
    private ProductRatingDto topRatedProduct;
    private Map<Integer, Long> starDistribution;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductSalesDto {
        private Integer productId;
        private String productName;
        private Long quantitySold;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BrandSalesDto {
        private String brandName;
        private Long quantitySold;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ColorStockDto {
        private String colorName;
        private Long stockQuantity;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductStockDto {
        private Integer productId;
        private String productName;
        private String colorName;
        private Integer stockQuantity;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerStatsDto {
        private Integer accountId;
        private String username;
        private Long orderCount;
        private BigDecimal totalSpent;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductRatingDto {
        private Integer productId;
        private String productName;
        private Double averageRating;
        private Long reviewCount;
    }
}
