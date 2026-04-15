package com.example.project_datn_sd08_baseballcapsales.Controller;

import com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto.AdminReportDto;
import com.example.project_datn_sd08_baseballcapsales.Service.AdminReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/staff/reports")
@RequiredArgsConstructor
public class StaffReportController {

    private final AdminReportService adminReportService;

    @GetMapping("/me/today")
    public AdminReportDto.StaffTodayDashboardDto getMyToday(
            Authentication authentication,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return adminReportService.getMyTodayDashboard(authentication.getName(), date);
    }

    @GetMapping("/me/customers/summary")
    public List<AdminReportDto.CustomerSearchDto> getMyCustomerSummary(
            Authentication authentication,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return adminReportService.getMyCustomerSummary(authentication.getName(), keyword, date);
    }

    @GetMapping("/me/purchase-history")
    public List<AdminReportDto.CustomerPurchaseHistoryDto> getMyPurchaseHistory(
            Authentication authentication,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return adminReportService.getMyPurchaseHistory(authentication.getName(), keyword, date);
    }
}