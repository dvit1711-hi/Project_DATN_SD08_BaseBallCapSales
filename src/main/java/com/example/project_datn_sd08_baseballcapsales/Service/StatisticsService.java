package com.example.project_datn_sd08_baseballcapsales.Service;

import com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto.StatisticsDto;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.Review;
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
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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
        var payments = paymentRepository.findAll();
        var reviews = reviewRepository.findAll();
        var productColors = productColorRepository.findAll();
        var accounts = accountRepository.findAll();

        Long totalShipment = totalOrders;
        Long totalDelivery = orders.stream()
                .filter(o -> o.getStatus() != null && (
                        o.getStatus().equalsIgnoreCase("DELIVERED") ||
                        o.getStatus().equalsIgnoreCase("COMPLETED") ||
                        o.getStatus().equalsIgnoreCase("HOAN THANH") ||
                        o.getStatus().equalsIgnoreCase("ĐÃ GIAO")
                ))
                .count();
        Long pending = Math.max(0L, totalShipment - totalDelivery);
        Double deliveryRate = totalShipment > 0 ? (totalDelivery.doubleValue() * 100.0 / totalShipment.doubleValue()) : 0.0;

        Map<String, Long> ordersByStatus = orderRepository.countByStatus().stream()
                .collect(Collectors.toMap(row -> (String) row[0], row -> (Long) row[1]));

        Map<String, Long> ordersByDay = orderRepository.countByDay().stream()
                .collect(Collectors.toMap(row -> (String) row[0], row -> ((Number) row[1]).longValue(), (a, b) -> a, LinkedHashMap::new));

        Map<String, Long> ordersByMonth = orderRepository.countByMonth().stream()
                .collect(Collectors.toMap(row -> (String) row[0], row -> ((Number) row[1]).longValue(), (a, b) -> a, LinkedHashMap::new));

        BigDecimal averageOrderValue = orderRepository.getAverageOrderValue();
        if (averageOrderValue == null) {
            averageOrderValue = BigDecimal.ZERO;
        }

        List<StatisticsDto.ProductSalesDto> topProducts = orderDetailRepository.topProductsByQuantity().stream()
                .limit(10)
                .map(row -> {
                    Integer pid = (Integer) row[0];
                    Long sold = ((Number) row[1]).longValue();
                    String pname = productRepository.findById(pid).map(p -> p.getProductName()).orElse("Unknown");
                    return new StatisticsDto.ProductSalesDto(pid, pname, sold);
                })
                .toList();

        List<StatisticsDto.BrandSalesDto> topBrands = orderDetailRepository.topBrandsByQuantity().stream()
                .limit(10)
                .map(row -> {
                    String brandName = (String) row[0];
                    Long sold = ((Number) row[1]).longValue();
                    return new StatisticsDto.BrandSalesDto(brandName, sold);
                })
                .toList();

        Map<String, Long> inventoryByColorMap = productColors.stream()
                .filter(pc -> pc.getColor() != null && pc.getColor().getColorName() != null)
                .collect(Collectors.groupingBy(pc -> pc.getColor().getColorName(), Collectors.summingLong(pc -> pc.getStockQuantity() == null ? 0 : pc.getStockQuantity())));

        List<StatisticsDto.ColorStockDto> inventoryByColor = inventoryByColorMap.entrySet().stream()
                .map(e -> new StatisticsDto.ColorStockDto(e.getKey(), e.getValue()))
                .toList();

        List<StatisticsDto.ProductStockDto> lowStockProducts = productColors.stream()
                .filter(pc -> pc.getStockQuantity() != null && pc.getStockQuantity() <= 10)
                .map(pc -> new StatisticsDto.ProductStockDto(
                        pc.getProduct() != null ? pc.getProduct().getId() : null,
                        pc.getProduct() != null ? pc.getProduct().getProductName() : "Unknown",
                        pc.getColor() != null ? pc.getColor().getColorName() : "Unknown",
                        pc.getStockQuantity()))
                .sorted(Comparator.comparingInt(p -> p.getStockQuantity() == null ? Integer.MAX_VALUE : p.getStockQuantity()))
                .limit(20)
                .toList();

        Map<Integer, Long> ordersByAccount = orders.stream()
                .filter(o -> o.getAccount() != null)
                .collect(Collectors.groupingBy(o -> o.getAccount().getId(), Collectors.counting()));

        Map<Integer, BigDecimal> moneyByAccount = orders.stream()
                .filter(o -> o.getAccount() != null && o.getTotalAmount() != null)
                .collect(Collectors.groupingBy(o -> o.getAccount().getId(), Collectors.reducing(BigDecimal.ZERO, o -> o.getTotalAmount(), BigDecimal::add)));

        StatisticsDto.CustomerStatsDto topCustomerByOrders = ordersByAccount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(entry -> {
                    Integer aid = entry.getKey();
                    var account = accountRepository.findById(aid).orElse(null);
                    String uname = account != null ? account.getUsername() : "Unknown";
                    return new StatisticsDto.CustomerStatsDto(aid, uname, entry.getValue(), moneyByAccount.getOrDefault(aid, BigDecimal.ZERO));
                })
                .orElse(null);

        StatisticsDto.CustomerStatsDto topCustomerBySpending = moneyByAccount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(entry -> {
                    Integer aid = entry.getKey();
                    var account = accountRepository.findById(aid).orElse(null);
                    String uname = account != null ? account.getUsername() : "Unknown";
                    return new StatisticsDto.CustomerStatsDto(aid, uname, ordersByAccount.getOrDefault(aid, 0L), entry.getValue());
                })
                .orElse(null);

        Map<String, Long> accountStatusCounts = accounts.stream()
                .collect(Collectors.groupingBy(a -> {
                    var st = a.getStatus();
                    if (st == null || st.getStatusName() == null) return "UNKNOWN";
                    return st.getStatusName().toUpperCase();
                }, Collectors.counting()));

        Map<String, Long> paymentMethodCounts = paymentRepository.countByMethod().stream()
                .collect(Collectors.toMap(row -> (String) row[0], row -> ((Number) row[1]).longValue()));

        Map<String, Long> paymentStatusCounts = paymentRepository.countByStatus().stream()
                .collect(Collectors.toMap(row -> (String) row[0], row -> ((Number) row[1]).longValue()));

        BigDecimal totalPaidAmount = paymentRepository.totalSuccessfulPayment();
        if (totalPaidAmount == null) {
            totalPaidAmount = BigDecimal.ZERO;
        }

        double averageRating = reviewRepository.averageRating();
        if (averageRating < 0) {
            averageRating = 0;
        }

        List<StatisticsDto.ProductRatingDto> ratingByProduct = reviewRepository.ratingByProduct().stream()
                .map(row -> {
                    Integer pid = (Integer) row[0];
                    Double avg = row[1] == null ? 0.0 : ((Number) row[1]).doubleValue();
                    Long count = row[2] == null ? 0L : ((Number) row[2]).longValue();
                    String pname = productRepository.findById(pid).map(p -> p.getProductName()).orElse("Unknown");
                    return new StatisticsDto.ProductRatingDto(pid, pname, avg, count);
                })
                .toList();

        StatisticsDto.ProductRatingDto topRatedProduct = ratingByProduct.stream()
                .max(Comparator.comparing(StatisticsDto.ProductRatingDto::getAverageRating).thenComparing(StatisticsDto.ProductRatingDto::getReviewCount))
                .orElse(null);

        Map<Integer, Long> starDistribution = reviewRepository.distributionByStar().stream()
                .collect(Collectors.toMap(row -> ((Number) row[0]).intValue(), row -> ((Number) row[1]).longValue()));

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
