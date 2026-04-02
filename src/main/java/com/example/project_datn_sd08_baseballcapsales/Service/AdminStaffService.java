package com.example.project_datn_sd08_baseballcapsales.Service;

import com.example.project_datn_sd08_baseballcapsales.payload.request.CreateStaffAccountRequest;
import com.example.project_datn_sd08_baseballcapsales.payload.reponse.CreateStaffAccountResponse;
import com.example.project_datn_sd08_baseballcapsales.payload.reponse.OptionResponse;

import java.util.List;

public interface AdminStaffService {
    List<OptionResponse> getStatuses();
    CreateStaffAccountResponse createStaffAccount(CreateStaffAccountRequest request);
}