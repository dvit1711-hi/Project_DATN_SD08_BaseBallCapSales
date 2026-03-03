package com.example.project_datn_sd08_baseballcapsales.Controller;

import com.example.project_datn_sd08_baseballcapsales.Model.entity.Order;
import com.example.project_datn_sd08_baseballcapsales.Service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/checkout/cod")
    public Order checkoutCOD(@RequestParam Integer accountId) {
        return paymentService.checkoutCOD(accountId);
    }
}