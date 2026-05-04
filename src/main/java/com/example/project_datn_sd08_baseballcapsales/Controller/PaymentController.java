package com.example.project_datn_sd08_baseballcapsales.Controller;

import com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto.GetPaidOrderWithDetailsDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.payload.request.PostCompletedReturnRequest;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.payload.request.PostShippingReturnDto;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.Order;
import com.example.project_datn_sd08_baseballcapsales.Model.enums.OrderStatus;
import com.example.project_datn_sd08_baseballcapsales.Model.enums.PaymentStatus;
import com.example.project_datn_sd08_baseballcapsales.Service.PaymentService;
import com.example.project_datn_sd08_baseballcapsales.payload.reponse.MBBankPaymentInfoResponse;
import com.example.project_datn_sd08_baseballcapsales.payload.request.CheckoutRequest;
import com.example.project_datn_sd08_baseballcapsales.payload.request.GhnShippingFeeRequest;
import com.example.project_datn_sd08_baseballcapsales.payload.request.RevertOrderStatusRequest;
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
        Order order = paymentService.checkout(
                request.getAccountId(),
                request.getMethod(),
                request.getCartItemIds(),
                request.getCouponCode(),
                request.getShippingFee()
        );
        return ResponseEntity.ok(buildCheckoutResponse(order));
    }

    @PostMapping("/shipping-fee/ghn")
    public ResponseEntity<Map<String, Object>> calculateGhnShippingFee(@RequestBody GhnShippingFeeRequest request) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("shippingFee", paymentService.calculateShippingFeeByGhn(request));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/ghn/provinces")
    public ResponseEntity<List<Map<String, Object>>> getGhnProvinces() {
        return ResponseEntity.ok(paymentService.getGhnProvinces());
    }

    @GetMapping("/ghn/districts")
    public ResponseEntity<List<Map<String, Object>>> getGhnDistricts(@RequestParam Integer provinceId) {
        return ResponseEntity.ok(paymentService.getGhnDistricts(provinceId));
    }

    @GetMapping("/ghn/wards")
    public ResponseEntity<List<Map<String, Object>>> getGhnWards(@RequestParam Integer districtId) {
        return ResponseEntity.ok(paymentService.getGhnWards(districtId));
    }

    @GetMapping("/account/{accountId}/orders")
    public ResponseEntity<List<GetPaidOrderWithDetailsDto>> getOrdersByAccount(@PathVariable Integer accountId) {
        return ResponseEntity.ok(paymentService.getOrdersWithDetailsForAccount(accountId));
    }

    @GetMapping("/orders")
    public ResponseEntity<List<GetPaidOrderWithDetailsDto>> getAllOrders() {
        return ResponseEntity.ok(paymentService.getAllOrdersWithDetails());
    }

    @GetMapping("/orders/{orderId}/mb-bank-info")
    public ResponseEntity<MBBankPaymentInfoResponse> getMBBankPaymentInfo(@PathVariable Integer orderId) {
        return ResponseEntity.ok(paymentService.getMBBankPaymentInfo(orderId));
    }

    @PutMapping("/orders/{orderId}/confirm")
    public ResponseEntity<Map<String, Object>> confirmPayment(@PathVariable Integer orderId) {
        Order order = paymentService.confirmPayment(orderId);

        PaymentStatus paymentStatus =
                paymentService.getLatestPaymentStatusForOrder(order.getId());

        return ResponseEntity.ok(
                buildOrderStatusResponse(
                        order.getId(),
                        order.getStatus(),
                        paymentStatus,
                        "Đã xác nhận thanh toán"
                )
        );
    }

    @PutMapping("/account/{accountId}/orders/{orderId}/cancel")
    public ResponseEntity<Map<String, Object>> cancelOrderForAccount(
            @PathVariable Integer accountId,
            @PathVariable Integer orderId
    ) {
        Order order = paymentService.cancelOrderForAccount(accountId, orderId);


        return ResponseEntity.ok(
                buildOrderStatusResponse(
                        order.getId(),
                        order.getStatus(),
                        PaymentStatus.CANCELLED,
                        "Đã hủy đơn hàng"
                )
        );
    }

    @PutMapping("/orders/{orderId}/cancel")
    public ResponseEntity<Map<String, Object>> cancelOrderByAdmin(@PathVariable Integer orderId) {
        Order order = paymentService.cancelOrderByAdmin(orderId);

        return ResponseEntity.ok(
                buildOrderStatusResponse(
                        order.getId(),
                        order.getStatus(),
                        PaymentStatus.CANCELLED,
                        "Đã hủy đơn hàng"
                )
        );
    }

    @PutMapping("/orders/{orderId}/revert")
    public ResponseEntity<Map<String, Object>> revertOrderStatusByAdmin(
            @PathVariable Integer orderId,
            @RequestBody RevertOrderStatusRequest request
    ) {
        Order order = paymentService.revertOrderStatusByAdmin(
                orderId,
                request != null ? request.getReason() : null
        );

        PaymentStatus paymentStatus =
                paymentService.getLatestPaymentStatusForOrder(order.getId());

        return ResponseEntity.ok(
                buildOrderStatusResponse(
                        order.getId(),
                        order.getStatus(),
                        paymentStatus,
                        "Đã quay lại trạng thái trước"
                )
        );
    }

    @PutMapping("/orders/{orderId}/start-shipping")
    public ResponseEntity<Map<String, Object>> startShippingByAdmin(@PathVariable Integer orderId) {
        Order order = paymentService.startShippingByAdmin(orderId);

        PaymentStatus paymentStatus =
                paymentService.getLatestPaymentStatusForOrder(order.getId());

        return ResponseEntity.ok(
                buildOrderStatusResponse(
                        order.getId(),
                        order.getStatus(),
                        paymentStatus,
                        "Đã chuyển sang chờ giao hàng"
                )
        );
    }

    @PutMapping("/orders/{orderId}/complete-delivery")
    public ResponseEntity<Map<String, Object>> completeDeliveryByAdmin(@PathVariable Integer orderId) {
        Order order = paymentService.completeDeliveryByAdmin(orderId);

        PaymentStatus paymentStatus =
                paymentService.getLatestPaymentStatusForOrder(order.getId());

        return ResponseEntity.ok(
                buildOrderStatusResponse(
                        order.getId(),
                        order.getStatus(),
                        paymentStatus,
                        "Đã giao thành công"
                )
        );
    }

    private Map<String, Object> buildCheckoutResponse(Order order) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("orderId", order.getId());
        response.put("id", order.getId());
        response.put("status", order.getStatus());
        response.put("orderStatus", order.getStatus());
        response.put("orderType", order.getOrderType());
        response.put("totalAmount", order.getTotalAmount());
        response.put("trackingCode", order.getTrackingCode());
        response.put("customerName", order.getCustomerName());
        response.put("customerPhone", order.getCustomerPhone());
        return response;
    }

    private Map<String, Object> buildOrderStatusResponse(
            Integer orderId,
            OrderStatus orderStatus,
            PaymentStatus paymentStatus,
            String message
    ) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("orderId", orderId);
        response.put("status", orderStatus);         // enum trả ra JSON = string OK
        response.put("orderStatus", orderStatus);
        response.put("paymentStatus", paymentStatus);
        response.put("message", message);
        return response;
    }

    @GetMapping("/orders/return-search")
    public ResponseEntity<GetPaidOrderWithDetailsDto> findCompletedReturnableOrderByCode(
            @RequestParam String code
    ) {
        return ResponseEntity.ok(paymentService.findCompletedReturnableOrderByCode(code));
    }

    @PostMapping("/orders/{orderId}/shipping-returns")
    public ResponseEntity<GetPaidOrderWithDetailsDto> returnShippingOrderItemByAdmin(
            @PathVariable Integer orderId,
            @RequestBody PostShippingReturnDto request
    ) {
        return ResponseEntity.ok(paymentService.returnShippingOrderItemByAdmin(orderId, request));
    }

    @PostMapping("/orders/{orderId}/completed-returns")
    public ResponseEntity<GetPaidOrderWithDetailsDto> returnCompletedOrderByAdmin(
            @PathVariable Integer orderId,
            @RequestBody PostCompletedReturnRequest request
    ) {
        return ResponseEntity.ok(paymentService.returnCompletedOrderByAdmin(orderId, request));
    }
}