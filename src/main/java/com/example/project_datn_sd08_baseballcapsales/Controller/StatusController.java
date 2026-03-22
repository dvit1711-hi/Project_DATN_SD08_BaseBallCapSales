package com.example.project_datn_sd08_baseballcapsales.Controller;

import com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto.GetStatusDto;
import com.example.project_datn_sd08_baseballcapsales.Service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/status")
public class StatusController {

    @Autowired
    private AccountService accountService;

    @GetMapping
    public List<GetStatusDto> getAllStatuses() {
        return accountService.getAllStatuses();
    }
}