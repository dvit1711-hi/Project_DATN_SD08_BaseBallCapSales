package com.example.project_datn_sd08_baseballcapsales.Service;

import com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto.StatisticsDto;
import com.example.project_datn_sd08_baseballcapsales.Model.enums.OrderStatus;
import com.example.project_datn_sd08_baseballcapsales.Repository.AccountRepository;
import com.example.project_datn_sd08_baseballcapsales.Repository.OrderDetailRepository;
import com.example.project_datn_sd08_baseballcapsales.Repository.OrderRepository;
import com.example.project_datn_sd08_baseballcapsales.Repository.PaymentRepository;
import com.example.project_datn_sd08_baseballcapsales.Repository.ProductColorRepository;
import com.example.project_datn_sd08_baseballcapsales.Repository.ProductRepository;
import com.example.project_datn_sd08_baseballcapsales.Repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatisticsService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ProductColorRepository productColorRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ProductRepository productRepository;

    public StatisticsDto getDashboardStatistics() {
        Long totalOrders = orderRepository.count();
        Long totalCustomers = accountRepository.count();
        Long totalProducts = productRepository.count();
        BigDecimal totalRevenue = orderRepository.getTotalRevenue();

        var orders = orderRepository.findAll();
        var orderDetails = orderDetailRepository.findAll();
        var productColors = productColorRepository.findAll();
        var accounts = accountRepository.findAll();

        Long totalShipment = totalOrders;
        Long totalDelivery = orders.stream()
                .filter(o -> o.getStatus() != null && (
                        o.getStatus() == OrderStatus.DELIVERED ||
                                o.getStatus() == OrderStatus.COMPLETED ||
                                o.getStatus() == OrderStatus.HOAN_THANH ||
                                o.getStatus() == OrderStatus.DA_GIAO
                ))
                .count();

        Long pending = Math.max(0L, totalShipment - totalDelivery);
        Double deliveryRate = totalShipment > 0
                ? (totalDelivery.doubleValue() * 100.0 / totalShipment.doubleValue())
                : 0.0;

        Map<String, Long> ordersByStatus = orderRepository.countByStatus().stream()
                .collect(Collectors.toMap(
                        row -> String.valueOf(row[0]),
                        row -> row[1] == null ? 0L : ((Number) row[1]).longValue(),
                        (a, b) -> a,
                        LinkedHashMap::new
                ));

        Map<String, Long> ordersByDay = orderRepository.countByDay().stream()
                .collect(Collectors.toMap(
                        row -> String.valueOf(row[0]),
                        row -> row[1] == null ? 0L : ((Number) row[1]).longValue(),
                        (a, b) -> a,
                        LinkedHashMap::new
                ));

        Map<String, Long> ordersByMonth = orderRepository.countByMonth().stream()
                .collect(Collectors.toMap(
                        row -> String.valueOf(row[0]),
                        row -> row[1] == null ? 0L : ((Number) row[1]).longValue(),
                        (a, b) -> a,
                        LinkedHashMap::new
                ));

        BigDecimal averageOrderValue = orderRepository.getAverageOrderValue();
        if (averageOrderValue == null) {
            averageOrderValue = BigDecimal.ZERO;
        }

        // Top products: group theo PRODUCT thật, không phải productColor
        Map<Integer, Long> productSalesMap = orderDetails.stream()
                .filter(od -> od.getProductColorID() != null
                        && od.getProductColorID().getProductID() != null
                        && od.getProductColorID().getProductID().getId() != null
                        && od.getQuantity() != null)
                .collect(Collectors.groupingBy(
                        od -> od.getProductColorID().getProductID().getId(),
                        Collectors.summingLong(od -> od.getQuantity().longValue())
                ));

        List<StatisticsDto.ProductSalesDto> topProducts = productSalesMap.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .limit(10)
                .map(entry -> {
                    Integer productId = entry.getKey();
                    Long quantitySold = entry.getValue();
                    String productName = productRepository.findById(productId)
                            .map(p -> p.getProductName())
                            .orElse("Unknown");
                    return new StatisticsDto.ProductSalesDto(productId, productName, quantitySold);
                })
                .toList();

        List<StatisticsDto.BrandSalesDto> topBrands = orderDetailRepository.topBrandsByQuantity().stream()
                .limit(10)
                .map(row -> new StatisticsDto.BrandSalesDto(
                        String.valueOf(row[0]),
                        row[1] == null ? 0L : ((Number) row[1]).longValue()
                ))
                .toList();

        Map<String, Long> inventoryByColorMap = productColors.stream()
                .filter(pc -> pc.getColorID() != null && pc.getColorID().getColorName() != null)
                .collect(Collectors.groupingBy(
                        pc -> pc.getColorID().getColorName(),
                        Collectors.summingLong(pc -> pc.getStockQuantity() == null ? 0 : pc.getStockQuantity())
                ));

        List<StatisticsDto.ColorStockDto> inventoryByColor = inventoryByColorMap.entrySet().stream()
                .map(e -> new StatisticsDto.ColorStockDto(e.getKey(), e.getValue()))
                .toList();

        List<StatisticsDto.ProductStockDto> lowStockProducts = productColors.stream()
                .filter(pc -> pc.getStockQuantity() != null && pc.getStockQuantity() <= 10)
                .map(pc -> new StatisticsDto.ProductStockDto(
                        pc.getProductID() != null ? pc.getProductID().getId() : null,
                        pc.getProductID() != null ? pc.getProductID().getProductName() : "Unknown",
                        pc.getColorID() != null ? pc.getColorID().getColorName() : "Unknown",
                        pc.getStockQuantity()
                ))
                .sorted(Comparator.comparingInt(p -> p.getStockQuantity() == null ? Integer.MAX_VALUE : p.getStockQuantity()))
                .limit(20)
                .toList();

        Map<Integer, Long> ordersByAccount = orders.stream()
                .filter(o -> o.getAccountID() != null && o.getAccountID().getId() != null)
                .collect(Collectors.groupingBy(
                        o -> o.getAccountID().getId(),
                        Collectors.counting()
                ));

        Map<Integer, BigDecimal> moneyByAccount = orders.stream()
                .filter(o -> o.getAccountID() != null && o.getAccountID().getId() != null && o.getTotalAmount() != null)
                .collect(Collectors.groupingBy(
                        o -> o.getAccountID().getId(),
                        Collectors.reducing(BigDecimal.ZERO, o -> o.getTotalAmount(), BigDecimal::add)
                ));

        StatisticsDto.CustomerStatsDto topCustomerByOrders = ordersByAccount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(entry -> {
                    Integer aid = entry.getKey();
                    var account = accountRepository.findById(aid).orElse(null);
                    String uname = account != null ? account.getUsername() : "Unknown";
                    return new StatisticsDto.CustomerStatsDto(
                            aid,
                            uname,
                            entry.getValue(),
                            moneyByAccount.getOrDefault(aid, BigDecimal.ZERO)
                    );
                })
                .orElse(null);

        StatisticsDto.CustomerStatsDto topCustomerBySpending = moneyByAccount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(entry -> {
                    Integer aid = entry.getKey();
                    var account = accountRepository.findById(aid).orElse(null);
                    String uname = account != null ? account.getUsername() : "Unknown";
                    return new StatisticsDto.CustomerStatsDto(
                            aid,
                            uname,
                            ordersByAccount.getOrDefault(aid, 0L),
                            entry.getValue()
                    );
                })
                .orElse(null);

        Map<String, Long> accountStatusCounts = accounts.stream()
                .collect(Collectors.groupingBy(a -> {
                    var st = a.getStatus();
                    if (st == null || st.getStatusName() == null) return "UNKNOWN";
                    return st.getStatusName().toUpperCase();
                }, Collectors.counting()));

        Map<String, Long> paymentMethodCounts = paymentRepository.countByMethod().stream()
                .collect(Collectors.toMap(
                        row -> String.valueOf(row[0]),
                        row -> row[1] == null ? 0L : ((Number) row[1]).longValue(),
                        (a, b) -> a,
                        LinkedHashMap::new
                ));

        Map<String, Long> paymentStatusCounts = paymentRepository.countByStatus().stream()
                .collect(Collectors.toMap(
                        row -> String.valueOf(row[0]),
                        row -> row[1] == null ? 0L : ((Number) row[1]).longValue(),
                        (a, b) -> a,
                        LinkedHashMap::new
                ));

        BigDecimal totalPaidAmount = paymentRepository.totalSuccessfulPayment();
        if (totalPaidAmount == null) {
            totalPaidAmount = BigDecimal.ZERO;
        }

        Double avgRatingValue = reviewRepository.averageRating();
        double averageRating = avgRatingValue == null ? 0.0 : avgRatingValue;
        if (averageRating < 0) {
            averageRating = 0;
        }

        List<StatisticsDto.ProductRatingDto> ratingByProduct = reviewRepository.ratingByProduct().stream()
                .map(row -> {
                    Integer pid = row[0] == null ? null : ((Number) row[0]).intValue();
                    Double avg = row[1] == null ? 0.0 : ((Number) row[1]).doubleValue();
                    Long count = row[2] == null ? 0L : ((Number) row[2]).longValue();
                    String pname = pid == null
                            ? "Unknown"
                            : productRepository.findById(pid).map(p -> p.getProductName()).orElse("Unknown");
                    return new StatisticsDto.ProductRatingDto(pid, pname, avg, count);
                })
                .toList();

        StatisticsDto.ProductRatingDto topRatedProduct = ratingByProduct.stream()
                .max(Comparator.comparing(StatisticsDto.ProductRatingDto::getAverageRating)
                        .thenComparing(StatisticsDto.ProductRatingDto::getReviewCount))
                .orElse(null);

        Map<Integer, Long> starDistribution = reviewRepository.distributionByStar().stream()
                .collect(Collectors.toMap(
                        row -> row[0] == null ? 0 : ((Number) row[0]).intValue(),
                        row -> row[1] == null ? 0L : ((Number) row[1]).longValue(),
                        (a, b) -> a,
                        LinkedHashMap::new
                ));

        StatisticsDto dto = new StatisticsDto();
        dto.setTotalOrders(totalOrders);
        dto.setTotalRevenue(totalRevenue == null ? BigDecimal.ZERO : totalRevenue);
        dto.setTotalCustomers(totalCustomers);
        dto.setTotalProducts(totalProducts);

        dto.setTotalShipment(totalShipment);
        dto.setTotalDelivery(totalDelivery);
        dto.setPending(pending);
        dto.setDeliveryRate(Math.round(deliveryRate * 100.0) / 100.0);

        dto.setOrdersByStatus(ordersByStatus);
        dto.setOrdersByDay(ordersByDay);
        dto.setOrdersByMonth(ordersByMonth);
        dto.setAverageOrderValue(averageOrderValue);

        dto.setTopProducts(topProducts);
        dto.setTopBrands(topBrands);
        dto.setInventoryByColor(inventoryByColor);
        dto.setLowStockProducts(lowStockProducts);

        dto.setTopCustomerByOrders(topCustomerByOrders);
        dto.setTopCustomerBySpending(topCustomerBySpending);
        dto.setAccountStatusCounts(accountStatusCounts);

        dto.setPaymentMethodCounts(paymentMethodCounts);
        dto.setPaymentStatusCounts(paymentStatusCounts);
        dto.setTotalPaidAmount(totalPaidAmount);

        dto.setAverageRating(Math.round(averageRating * 100.0) / 100.0);
        dto.setTopRatedProduct(topRatedProduct);
        dto.setStarDistribution(starDistribution);

        return dto;
    }
}