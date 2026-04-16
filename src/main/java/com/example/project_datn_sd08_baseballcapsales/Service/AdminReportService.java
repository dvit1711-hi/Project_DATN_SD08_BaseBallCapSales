package com.example.project_datn_sd08_baseballcapsales.Service;

import com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto.AdminReportDto;

import java.time.LocalDate;
import java.util.List;

public interface AdminReportService {
    List<AdminReportDto.StaffOptionDto> getStaffOptions();

    List<AdminReportDto.StaffTodayItemDto> getStaffTodayOverview(Integer employeeId, LocalDate reportDate);
    AdminReportDto.StaffTodayDashboardDto getStaffTodayDashboard(Integer employeeId, LocalDate reportDate);

    List<AdminReportDto.CustomerSearchDto> getCustomerSummary(String keyword, Integer employeeId, LocalDate reportDate);
    List<AdminReportDto.CustomerPurchaseHistoryDto> getPurchaseHistory(String keyword, Integer employeeId, LocalDate reportDate);

    AdminReportDto.StaffTodayDashboardDto getMyTodayDashboard(String email, LocalDate reportDate);
    List<AdminReportDto.CustomerSearchDto> getMyCustomerSummary(String email, String keyword, LocalDate reportDate);
    List<AdminReportDto.CustomerPurchaseHistoryDto> getMyPurchaseHistory(String email, String keyword, LocalDate reportDate);


    AdminReportDto.StaffMonthSummaryDto getStaffMonthSummary(Integer employeeId, LocalDate date);
}