package com.example.project_datn_sd08_baseballcapsales.Service.Impl;

import com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto.AdminReportDto;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.Account;
import com.example.project_datn_sd08_baseballcapsales.Repository.AccountRepository;
import com.example.project_datn_sd08_baseballcapsales.Repository.AdminReportRepository;
import com.example.project_datn_sd08_baseballcapsales.Service.AdminReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminReportServiceImpl implements AdminReportService {
    @Autowired
    private AdminReportRepository adminReportRepository;
    @Autowired
    private AccountRepository accountRepository;

    @Override
    public List<AdminReportDto.StaffOptionDto> getStaffOptions() {
        return adminReportRepository.findStaffOptions().stream()
                .map(x -> new AdminReportDto.StaffOptionDto(
                        x.getEmployeeId(),
                        x.getUsername(),
                        x.getEmail(),
                        x.getUsername() + " - " + x.getEmail()
                ))
                .toList();
    }

    @Override
    public List<AdminReportDto.StaffTodayItemDto> getStaffTodayOverview(Integer employeeId, LocalDate reportDate) {
        LocalDate targetDate = resolveDate(reportDate);

        return adminReportRepository.findStaffTodayOverview(employeeId, targetDate).stream()
                .map(x -> new AdminReportDto.StaffTodayItemDto(
                        x.getEmployeeId(),
                        x.getEmployeeName(),
                        x.getEmail(),
                        nvlLong(x.getTotalOrdersToday()),
                        nvlLong(x.getTotalProductsToday()),
                        nvlMoney(x.getTotalRevenueToday())
                ))
                .toList();
    }

    @Override
    public AdminReportDto.StaffTodayDashboardDto getStaffTodayDashboard(Integer employeeId, LocalDate reportDate) {
        LocalDate targetDate = resolveDate(reportDate);

        if (employeeId == null) {
            List<AdminReportDto.StaffTodayItemDto> items = getStaffTodayOverview(null, targetDate);

            long totalOrders = items.stream().mapToLong(x -> nvlLong(x.getTotalOrdersToday())).sum();
            long totalProducts = items.stream().mapToLong(x -> nvlLong(x.getTotalProductsToday())).sum();
            BigDecimal totalRevenue = items.stream()
                    .map(AdminReportDto.StaffTodayItemDto::getTotalRevenueToday)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            AdminReportDto.StaffTodayDashboardDto dto = new AdminReportDto.StaffTodayDashboardDto();
            dto.setEmployeeId(null);
            dto.setEmployeeName("Tất cả nhân viên");
            dto.setTotalOrdersToday(totalOrders);
            dto.setTotalProductsToday(totalProducts);
            dto.setTotalRevenueToday(totalRevenue);
            dto.setRecentOrders(getPurchaseHistory("", null, targetDate).stream().limit(20).toList());
            return dto;
        }

        AdminReportDto.StaffTodayItemDto metric = getStaffTodayOverview(employeeId, targetDate).stream()
                .findFirst()
                .orElse(new AdminReportDto.StaffTodayItemDto(employeeId, "", "", 0L, 0L, BigDecimal.ZERO));

        AdminReportDto.StaffTodayDashboardDto dto = new AdminReportDto.StaffTodayDashboardDto();
        dto.setEmployeeId(metric.getEmployeeId());
        dto.setEmployeeName(metric.getEmployeeName());
        dto.setTotalOrdersToday(metric.getTotalOrdersToday());
        dto.setTotalProductsToday(metric.getTotalProductsToday());
        dto.setTotalRevenueToday(metric.getTotalRevenueToday());
        dto.setRecentOrders(getPurchaseHistory("", employeeId, targetDate).stream().limit(20).toList());
        return dto;
    }

    @Override
    public List<AdminReportDto.CustomerSearchDto> getCustomerSummary(String keyword, Integer employeeId, LocalDate reportDate) {
        String safeKeyword = keyword == null ? "" : keyword.trim();
        LocalDate targetDate = resolveDate(reportDate);

        return adminReportRepository.findCustomerSummary(safeKeyword, employeeId, targetDate).stream()
                .map(x -> new AdminReportDto.CustomerSearchDto(
                        x.getCustomerId(),
                        x.getCustomerName(),
                        x.getCustomerPhone(),
                        x.getCustomerType(),
                        nvlLong(x.getTotalOrders()),
                        nvlMoney(x.getTotalSpent())
                ))
                .toList();
    }

    @Override
    public List<AdminReportDto.CustomerPurchaseHistoryDto> getPurchaseHistory(String keyword, Integer employeeId, LocalDate reportDate) {
        String safeKeyword = keyword == null ? "" : keyword.trim();
        LocalDate targetDate = resolveDate(reportDate);

        List<AdminReportRepository.PurchaseHistoryRowProjection> rows =
                adminReportRepository.findPurchaseHistoryRows(safeKeyword, employeeId, targetDate);

        Map<Integer, AdminReportDto.CustomerPurchaseHistoryDto> map = new LinkedHashMap<>();

        for (AdminReportRepository.PurchaseHistoryRowProjection row : rows) {
            AdminReportDto.CustomerPurchaseHistoryDto orderDto = map.computeIfAbsent(
                    row.getOrderId(),
                    id -> {
                        AdminReportDto.CustomerPurchaseHistoryDto dto = new AdminReportDto.CustomerPurchaseHistoryDto();
                        dto.setOrderId(row.getOrderId());
                        dto.setTrackingCode(row.getTrackingCode());
                        dto.setOrderDate(row.getOrderDate());
                        dto.setOrderType(row.getOrderType());
                        dto.setOrderStatus(row.getOrderStatus());

                        dto.setCustomerId(row.getCustomerId());
                        dto.setCustomerName(row.getCustomerName());
                        dto.setCustomerPhone(row.getCustomerPhone());

                        dto.setEmployeeId(row.getEmployeeId());
                        dto.setEmployeeName(row.getEmployeeName());

                        dto.setTotalAmount(nvlMoney(row.getTotalAmount()));
                        dto.setPaymentMethod(row.getPaymentMethod());
                        dto.setPaymentStatus(row.getPaymentStatus());
                        dto.setTotalItems(0);
                        dto.setItems(new ArrayList<>());
                        return dto;
                    }
            );

            if (row.getOrderDetailId() != null) {
                Integer quantity = row.getQuantity() == null ? 0 : row.getQuantity();
                BigDecimal price = nvlMoney(row.getPrice());

                orderDto.getItems().add(new AdminReportDto.OrderItemLiteDto(
                        row.getOrderDetailId(),
                        row.getProductColorId(),
                        row.getProductName(),
                        row.getColorName(),
                        row.getSizeName(),
                        quantity,
                        price,
                        price.multiply(BigDecimal.valueOf(quantity))
                ));

                orderDto.setTotalItems(orderDto.getTotalItems() + quantity);
            }
        }

        return new ArrayList<>(map.values());
    }

    @Override
    public AdminReportDto.StaffTodayDashboardDto getMyTodayDashboard(String email, LocalDate reportDate) {
        Account me = accountRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy staff"));
        return getStaffTodayDashboard(me.getId(), reportDate);
    }

    @Override
    public List<AdminReportDto.CustomerSearchDto> getMyCustomerSummary(String email, String keyword, LocalDate reportDate) {
        Account me = accountRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy staff"));
        return getCustomerSummary(keyword, me.getId(), reportDate);
    }

    @Override
    public List<AdminReportDto.CustomerPurchaseHistoryDto> getMyPurchaseHistory(String email, String keyword, LocalDate reportDate) {
        Account me = accountRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy staff"));
        return getPurchaseHistory(keyword, me.getId(), reportDate);
    }

    private LocalDate resolveDate(LocalDate reportDate) {
        return reportDate != null ? reportDate : LocalDate.now();
    }

    private Long nvlLong(Long value) {
        return value == null ? 0L : value;
    }

    private BigDecimal nvlMoney(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}