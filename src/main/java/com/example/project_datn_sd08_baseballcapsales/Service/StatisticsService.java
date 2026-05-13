package com.example.project_datn_sd08_baseballcapsales.Service;

import com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto.StatisticsDto;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.Order;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.Payment;
import com.example.project_datn_sd08_baseballcapsales.Model.enums.OrderStatus;
import com.example.project_datn_sd08_baseballcapsales.Model.enums.PaymentStatus;
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
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
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

    // ── Helper: parse date string ─────────────────────────────────────────────
    private LocalDate parseDate(String s) {
        if (s == null || s.isBlank()) return null;
        return LocalDate.parse(s.trim()); // yyyy-MM-dd
    }

    // ── Helper: kiểm tra order có nằm trong khoảng ngày không ─────────────────
    private boolean inRange(LocalDate orderDate, LocalDate from, LocalDate to) {
        if (orderDate == null) return false;
        if (from != null && orderDate.isBefore(from)) return false;
        if (to != null && orderDate.isAfter(to)) return false;
        return true;
    }

    // ── Helper: lấy LocalDate từ Order ───────────────────────────────────────
    // Nếu entity dùng java.util.Date thì đổi thành:
    // return o.getCreatedAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    private LocalDate orderDate(Order o) {
        if (o.getOrderDate() == null) return null;
        return o.getOrderDate().toLocalDate(); // giả sử getCreatedAt() trả về LocalDateTime
    }

    // ── Method chính ──────────────────────────────────────────────────────────
    public StatisticsDto getDashboardStatistics(String dateFrom, String dateTo) {

        LocalDate from = parseDate(dateFrom);
        LocalDate to   = parseDate(dateTo);

        // Lấy tất cả dữ liệu
        var allOrders     = orderRepository.findAll();
        var orderDetails  = orderDetailRepository.findAll();
        var productColors = productColorRepository.findAll();
        var accounts      = accountRepository.findAll();

        // Filter orders theo khoảng ngày (nếu có)
        var orders = (from == null && to == null)
                ? allOrders
                : allOrders.stream()
                .filter(o -> inRange(orderDate(o), from, to))
                .collect(Collectors.toList());

        // Lấy set ID các order đã filter để filter orderDetails và payment
        Set<Integer> filteredOrderIds = orders.stream()
                .map(Order::getId)
                .collect(Collectors.toSet());

        // Filter orderDetails theo order đã lọc
        var filteredDetails = orderDetails.stream()
                .filter(od -> od.getOrderID() != null
                        && filteredOrderIds.contains(od.getOrderID().getId()))
                .collect(Collectors.toList());

        // ── Tổng quan ─────────────────────────────────────────────────────────
        Long totalOrders    = (long) orders.size();
        Long totalCustomers = accountRepository.count();
        Long totalProducts  = productRepository.count();

        BigDecimal totalRevenue = orders.stream()
                .map(o -> o.getTotalAmount() != null ? o.getTotalAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // ── Shipment / Delivery ───────────────────────────────────────────────
        Long totalShipment = totalOrders;
        Long totalDelivery = orders.stream()
                .filter(o -> o.getStatus() != null && (
                        o.getStatus() == OrderStatus.DELIVERED  ||
                                o.getStatus() == OrderStatus.COMPLETED  ||
                                o.getStatus() == OrderStatus.HOAN_THANH ||
                                o.getStatus() == OrderStatus.DA_GIAO))
                .count();
        Long   pending      = Math.max(0L, totalShipment - totalDelivery);
        Double deliveryRate = totalShipment > 0
                ? (totalDelivery.doubleValue() * 100.0 / totalShipment.doubleValue()) : 0.0;

        // ── Orders by status ──────────────────────────────────────────────────
        Map<String, Long> ordersByStatus = orders.stream()
                .filter(o -> o.getStatus() != null)
                .collect(Collectors.groupingBy(
                        o -> o.getStatus().name(),
                        LinkedHashMap::new,
                        Collectors.counting()));

        // ── Orders by day ─────────────────────────────────────────────────────
        Map<String, Long> ordersByDay = orders.stream()
                .filter(o -> orderDate(o) != null)
                .collect(Collectors.groupingBy(
                        o -> orderDate(o).toString(), // yyyy-MM-dd
                        LinkedHashMap::new,
                        Collectors.counting()));

        // ── Orders by month ───────────────────────────────────────────────────
        Map<String, Long> ordersByMonth = orders.stream()
                .filter(o -> orderDate(o) != null)
                .collect(Collectors.groupingBy(
                        o -> orderDate(o).getYear() + "-"
                                + String.format("%02d", orderDate(o).getMonthValue()),
                        LinkedHashMap::new,
                        Collectors.counting()));

        // ── Average order value ───────────────────────────────────────────────
        BigDecimal averageOrderValue = orders.isEmpty()
                ? BigDecimal.ZERO
                : totalRevenue.divide(BigDecimal.valueOf(orders.size()), 2, RoundingMode.HALF_UP);

        // ── Top products ──────────────────────────────────────────────────────
        Map<Integer, Long> productSalesMap = filteredDetails.stream()
                .filter(od -> od.getProductColorID() != null
                        && od.getProductColorID().getProductID() != null
                        && od.getProductColorID().getProductID().getId() != null
                        && od.getQuantity() != null)
                .collect(Collectors.groupingBy(
                        od -> od.getProductColorID().getProductID().getId(),
                        Collectors.summingLong(od -> od.getQuantity().longValue())));

        List<StatisticsDto.ProductSalesDto> topProducts = productSalesMap.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .limit(10)
                .map(entry -> {
                    Integer pid  = entry.getKey();
                    String  name = productRepository.findById(pid)
                            .map(p -> p.getProductName()).orElse("Unknown");
                    return new StatisticsDto.ProductSalesDto(pid, name, entry.getValue());
                })
                .toList();

        // ── Top brands ────────────────────────────────────────────────────────
        // Nếu bạn muốn giữ query gốc khi không filter ngày thì đổi đoạn này:
        // List<StatisticsDto.BrandSalesDto> topBrands = (from == null && to == null)
        //     ? orderDetailRepository.topBrandsByQuantity().stream()...
        //     : filteredDetails.stream()...
        Map<String, Long> brandSalesMap = filteredDetails.stream()
                .filter(od -> od.getProductColorID() != null
                        && od.getProductColorID().getProductID() != null
                        && od.getProductColorID().getProductID().getBrandID() != null
                        && od.getQuantity() != null)
                .collect(Collectors.groupingBy(
                        od -> od.getProductColorID().getProductID().getBrandID().getName(),
                        Collectors.summingLong(od -> od.getQuantity().longValue())));

        List<StatisticsDto.BrandSalesDto> topBrands = brandSalesMap.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .limit(10)
                .map(e -> new StatisticsDto.BrandSalesDto(e.getKey(), e.getValue()))
                .toList();

        // ── Inventory by color (không filter theo ngày) ───────────────────────
        Map<String, Long> inventoryByColorMap = productColors.stream()
                .filter(pc -> pc.getColorID() != null && pc.getColorID().getColorName() != null)
                .collect(Collectors.groupingBy(
                        pc -> pc.getColorID().getColorName(),
                        Collectors.summingLong(pc -> pc.getStockQuantity() == null ? 0 : pc.getStockQuantity())));

        List<StatisticsDto.ColorStockDto> inventoryByColor = inventoryByColorMap.entrySet().stream()
                .map(e -> new StatisticsDto.ColorStockDto(e.getKey(), e.getValue()))
                .toList();

        // ── Low stock (không filter theo ngày) ───────────────────────────────
        List<StatisticsDto.ProductStockDto> lowStockProducts = productColors.stream()
                .filter(pc -> pc.getStockQuantity() != null && pc.getStockQuantity() <= 10)
                .map(pc -> new StatisticsDto.ProductStockDto(
                        pc.getProductID() != null ? pc.getProductID().getId() : null,
                        pc.getProductID() != null ? pc.getProductID().getProductName() : "Unknown",
                        pc.getColorID() != null ? pc.getColorID().getColorName() : "Unknown",
                        pc.getStockQuantity()))
                .sorted(Comparator.comparingInt(
                        p -> p.getStockQuantity() == null ? Integer.MAX_VALUE : p.getStockQuantity()))
                .limit(20)
                .toList();

        // ── Top customers ─────────────────────────────────────────────────────
        Map<Integer, Long> ordersByAccount = orders.stream()
                .filter(o -> o.getAccountID() != null && o.getAccountID().getId() != null)
                .collect(Collectors.groupingBy(
                        o -> o.getAccountID().getId(),
                        Collectors.counting()));

        Map<Integer, BigDecimal> moneyByAccount = orders.stream()
                .filter(o -> o.getAccountID() != null
                        && o.getAccountID().getId() != null
                        && o.getTotalAmount() != null)
                .collect(Collectors.groupingBy(
                        o -> o.getAccountID().getId(),
                        Collectors.reducing(BigDecimal.ZERO, Order::getTotalAmount, BigDecimal::add)));

        StatisticsDto.CustomerStatsDto topCustomerByOrders = ordersByAccount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(e -> {
                    var acc = accountRepository.findById(e.getKey()).orElse(null);
                    return new StatisticsDto.CustomerStatsDto(
                            e.getKey(),
                            acc != null ? acc.getUsername() : "Unknown",
                            e.getValue(),
                            moneyByAccount.getOrDefault(e.getKey(), BigDecimal.ZERO));
                }).orElse(null);

        StatisticsDto.CustomerStatsDto topCustomerBySpending = moneyByAccount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(e -> {
                    var acc = accountRepository.findById(e.getKey()).orElse(null);
                    return new StatisticsDto.CustomerStatsDto(
                            e.getKey(),
                            acc != null ? acc.getUsername() : "Unknown",
                            ordersByAccount.getOrDefault(e.getKey(), 0L),
                            e.getValue());
                }).orElse(null);

        // ── Account status (không filter theo ngày) ───────────────────────────
        Map<String, Long> accountStatusCounts = accounts.stream()
                .collect(Collectors.groupingBy(a -> {
                    var st = a.getStatus();
                    return (st == null || st.getStatusName() == null)
                            ? "UNKNOWN" : st.getStatusName().toUpperCase();
                }, Collectors.counting()));

        // ── Payments ──────────────────────────────────────────────────────────
        Map<String, Long> paymentMethodCounts;
        Map<String, Long> paymentStatusCounts;
        BigDecimal        totalPaidAmount;

        if (from == null && to == null) {
            paymentMethodCounts = paymentRepository.countByMethod().stream()
                    .collect(Collectors.toMap(
                            row -> String.valueOf(row[0]),
                            row -> row[1] == null ? 0L : ((Number) row[1]).longValue(),
                            (a, b) -> a, LinkedHashMap::new));

            paymentStatusCounts = paymentRepository.countByStatus().stream()
                    .collect(Collectors.toMap(
                            row -> String.valueOf(row[0]),
                            row -> row[1] == null ? 0L : ((Number) row[1]).longValue(),
                            (a, b) -> a, LinkedHashMap::new));

            totalPaidAmount = paymentRepository.totalSuccessfulPayment();
            if (totalPaidAmount == null) totalPaidAmount = BigDecimal.ZERO;
        } else {
            // Có filter ngày → filter theo order ids
            var payments = paymentRepository.findAll().stream()
                    .filter(p -> p.getOrderID() != null
                            && filteredOrderIds.contains(p.getOrderID().getId()))
                    .collect(Collectors.toList());

            // method là String nên group thẳng
            paymentMethodCounts = payments.stream()
                    .filter(p -> p.getMethod() != null)
                    .collect(Collectors.groupingBy(
                            Payment::getMethod,
                            LinkedHashMap::new,
                            Collectors.counting()));

            // status là enum PaymentStatus
            paymentStatusCounts = payments.stream()
                    .filter(p -> p.getStatus() != null)
                    .collect(Collectors.groupingBy(
                            p -> p.getStatus().name(),
                            LinkedHashMap::new,
                            Collectors.counting()));

            totalPaidAmount = payments.stream()
                    .filter(p -> p.getStatus() == PaymentStatus.PAID)   // so sánh enum trực tiếp
                    .map(p -> p.getAmount() != null ? p.getAmount() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        if (totalPaidAmount == null) totalPaidAmount = BigDecimal.ZERO;

        // ── Reviews (không filter theo ngày) ──────────────────────────────────
        Double avgRatingValue = reviewRepository.averageRating();
        double averageRating  = avgRatingValue == null ? 0.0 : Math.max(0.0, avgRatingValue);

        List<StatisticsDto.ProductRatingDto> ratingByProduct = reviewRepository.ratingByProduct().stream()
                .map(row -> {
                    Integer pid   = row[0] == null ? null : ((Number) row[0]).intValue();
                    Double  avg   = row[1] == null ? 0.0  : ((Number) row[1]).doubleValue();
                    Long    count = row[2] == null ? 0L   : ((Number) row[2]).longValue();
                    String  pname = pid == null ? "Unknown"
                            : productRepository.findById(pid)
                            .map(p -> p.getProductName()).orElse("Unknown");
                    return new StatisticsDto.ProductRatingDto(pid, pname, avg, count);
                }).toList();

        StatisticsDto.ProductRatingDto topRatedProduct = ratingByProduct.stream()
                .max(Comparator.comparing(StatisticsDto.ProductRatingDto::getAverageRating)
                        .thenComparing(StatisticsDto.ProductRatingDto::getReviewCount))
                .orElse(null);

        Map<Integer, Long> starDistribution = reviewRepository.distributionByStar().stream()
                .collect(Collectors.toMap(
                        row -> row[0] == null ? 0 : ((Number) row[0]).intValue(),
                        row -> row[1] == null ? 0L : ((Number) row[1]).longValue(),
                        (a, b) -> a, LinkedHashMap::new));

        // ── Build DTO ─────────────────────────────────────────────────────────
        StatisticsDto dto = new StatisticsDto();
        dto.setTotalOrders(totalOrders);
        dto.setTotalRevenue(totalRevenue);
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