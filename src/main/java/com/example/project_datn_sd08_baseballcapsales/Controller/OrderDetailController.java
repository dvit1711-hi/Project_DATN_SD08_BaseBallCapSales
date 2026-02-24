package com.example.project_datn_sd08_baseballcapsales.Controller;

import com.example.project_datn_sd08_baseballcapsales.Model.dto.GetOrderDetailDto;
import com.example.project_datn_sd08_baseballcapsales.Service.OrderDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/order-detail")
public class OrderDetailController {
    @Autowired
    private OrderDetailService orderDetailService;

    @GetMapping
    public List<GetOrderDetailDto> getAll() {
        return orderDetailService.getAllOrderDetails();
    }
}
