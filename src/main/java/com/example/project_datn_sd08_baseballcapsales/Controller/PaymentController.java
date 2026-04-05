package com.example.project_datn_sd08_baseballcapsales.Controller;

import com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto.GetPaidOrderWithDetailsDto;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.Order;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.Payment;
import com.example.project_datn_sd08_baseballcapsales.Service.PaymentService;
import com.example.project_datn_sd08_baseballcapsales.payload.reponse.MBBankPaymentInfoResponse;
import com.example.project_datn_sd08_baseballcapsales.payload.request.CheckoutRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/checkout/cod")
    public ResponseEntity<Map<String, Object>> checkoutCOD(@RequestParam Integer accountId) {
        Order order = paymentService.checkoutCOD(accountId);
        return ResponseEntity.ok(buildCheckoutResponse(order));
    }

    @PostMapping("/checkout")
    public ResponseEntity<Map<String, Object>> checkout(
            @RequestParam Integer accountId,
            @RequestParam(defaultValue = "COD") String method
    ) {
        Order order = paymentService.checkout(accountId, method);
        return ResponseEntity.ok(buildCheckoutResponse(order));
    }

    @PostMapping("/checkout/selected")
    public ResponseEntity<Map<String, Object>> checkoutSelected(@RequestBody CheckoutRequest request) {
        Order order = paymentService.checkout(request.getAccountId(), request.getMethod(), request.getCartItemIds(), request.getCouponCode());
        return ResponseEntity.ok(buildCheckoutResponse(order));
    }

    @GetMapping("/account/{accountId}/orders")
    public List<GetPaidOrderWithDetailsDto> getOrdersByAccount(@PathVariable Integer accountId) {
        return paymentService.getOrdersWithDetailsForAccount(accountId);
    }

    @GetMapping("/orders")
    public List<GetPaidOrderWithDetailsDto> getAllOrders() {
        return paymentService.getAllOrdersWithDetails();
    }

    @GetMapping("/orders/{orderId}/mb-bank-info")
    public ResponseEntity<MBBankPaymentInfoResponse> getMBBankPaymentInfo(@PathVariable Integer orderId) {
        return ResponseEntity.ok(paymentService.getMBBankPaymentInfo(orderId));
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

    private Map<String, Object> buildCheckoutResponse(Order order) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("orderId", order.getId());
        response.put("id", order.getId());
        response.put("status", order.getStatus());
        response.put("totalAmount", order.getTotalAmount());
        return response;
    }
}