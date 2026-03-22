package com.example.project_datn_sd08_baseballcapsales.Controller;

import com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto.GetPaidOrderWithDetailsDto;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.Order;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.Payment;
import com.example.project_datn_sd08_baseballcapsales.Service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/checkout/cod")
    public Order checkoutCOD(@RequestParam Integer accountId) {
        return paymentService.checkoutCOD(accountId);
    }

    @PostMapping("/checkout")
    public Order checkout(
            @RequestParam Integer accountId,
            @RequestParam(defaultValue = "COD") String method
    ) {
        return paymentService.checkout(accountId, method);
    }

    @GetMapping("/account/{accountId}/orders")
    public List<GetPaidOrderWithDetailsDto> getOrdersByAccount(@PathVariable Integer accountId) {
        return paymentService.getOrdersWithDetailsForAccount(accountId);
    }

    @GetMapping("/orders")
    public List<GetPaidOrderWithDetailsDto> getAllOrders() {
        return paymentService.getAllOrdersWithDetails();
    }

    @PutMapping("/orders/{orderId}/confirm")
    public ResponseEntity<Payment> confirmPayment(@PathVariable Integer orderId) {
        return ResponseEntity.ok(paymentService.confirmPayment(orderId));
    }
}