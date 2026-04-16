package com.example.project_datn_sd08_baseballcapsales.Controller;

import com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto.AdminReportDto;
import com.example.project_datn_sd08_baseballcapsales.Service.AdminReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/admin/reports")
@RequiredArgsConstructor
public class AdminReportController {

    private final AdminReportService adminReportService;

    @GetMapping("/staff/options")
    public List<AdminReportDto.StaffOptionDto> getStaffOptions() {
        return adminReportService.getStaffOptions();
    }

    @GetMapping("/staff/overview-today")
    public List<AdminReportDto.StaffTodayItemDto> getStaffTodayOverview(
            @RequestParam(required = false) Integer employeeId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        System.out.println("overview date = " + date);
        return adminReportService.getStaffTodayOverview(employeeId, date);
    }

    @GetMapping("/staff/today")
    public AdminReportDto.StaffTodayDashboardDto getStaffTodayDashboard(
            @RequestParam(required = false) Integer employeeId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        System.out.println("dashboard date = " + date);
        return adminReportService.getStaffTodayDashboard(employeeId, date);
    }

    @GetMapping("/customers/summary")
    public List<AdminReportDto.CustomerSearchDto> getCustomerSummary(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(required = false) Integer employeeId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        System.out.println("customer summary date = " + date);
        return adminReportService.getCustomerSummary(keyword, employeeId, date);
    }

    @GetMapping("/customers/purchase-history")
    public List<AdminReportDto.CustomerPurchaseHistoryDto> getPurchaseHistory(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(required = false) Integer employeeId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        System.out.println("purchase history date = " + date);
        return adminReportService.getPurchaseHistory(keyword, employeeId, date);
    }

    @GetMapping("/staff/month-summary")
    public AdminReportDto.StaffMonthSummaryDto getStaffMonthSummary(
            @RequestParam(required = false) Integer employeeId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return adminReportService.getStaffMonthSummary(employeeId, date);
    }
}