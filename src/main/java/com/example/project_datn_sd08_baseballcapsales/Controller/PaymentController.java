package com.example.project_datn_sd08_baseballcapsales.Controller;

import com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto.GetPaidOrderWithDetailsDto;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.Order;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.Payment;
import com.example.project_datn_sd08_baseballcapsales.Service.PaymentService;
import com.example.project_datn_sd08_baseballcapsales.payload.request.CheckoutRequest;
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

    @PostMapping("/checkout/selected")
    public Order checkoutSelected(@RequestBody CheckoutRequest request) {
        return paymentService.checkout(request.getAccountId(), request.getMethod(), request.getCartItemIds(), request.getCouponCode());
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

    @PutMapping("/account/{accountId}/orders/{orderId}/cancel")
    public ResponseEntity<Order> cancelOrderForAccount(
            @PathVariable Integer accountId,
            @PathVariable Integer orderId
    ) {
        return ResponseEntity.ok(paymentService.cancelOrderForAccount(accountId, orderId));
    }

    @PutMapping("/orders/{orderId}/cancel")
    public ResponseEntity<Order> cancelOrderByAdmin(@PathVariable Integer orderId) {
        return ResponseEntity.ok(paymentService.cancelOrderByAdmin(orderId));
    }
}