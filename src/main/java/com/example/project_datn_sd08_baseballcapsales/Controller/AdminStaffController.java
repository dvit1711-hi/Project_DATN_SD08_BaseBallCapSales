package com.example.project_datn_sd08_baseballcapsales.Controller;

import com.example.project_datn_sd08_baseballcapsales.Service.AdminStaffService;
import com.example.project_datn_sd08_baseballcapsales.payload.request.CreateStaffAccountRequest;
import com.example.project_datn_sd08_baseballcapsales.payload.reponse.CreateStaffAccountResponse;
import com.example.project_datn_sd08_baseballcapsales.payload.reponse.OptionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/staff")
@RequiredArgsConstructor
public class AdminStaffController {

    private final AdminStaffService adminStaffService;

    @GetMapping("/statuses")
    public ResponseEntity<List<OptionResponse>> getStatuses() {
        return ResponseEntity.ok(adminStaffService.getStatuses());
    }

    @PostMapping
    public ResponseEntity<CreateStaffAccountResponse> createStaff(
            @RequestBody CreateStaffAccountRequest request
    ) {
        return ResponseEntity.ok(adminStaffService.createStaffAccount(request));
    }
}