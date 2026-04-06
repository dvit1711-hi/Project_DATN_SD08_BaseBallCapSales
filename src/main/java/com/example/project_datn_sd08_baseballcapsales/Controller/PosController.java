package com.example.project_datn_sd08_baseballcapsales.Controller;

import com.example.project_datn_sd08_baseballcapsales.Model.dto.PostDto.*;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.PutDto.PutOfflineOrderInfoDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.PutDto.PutOfflineOrderItemDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto.*;
import com.example.project_datn_sd08_baseballcapsales.Service.PosService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/pos")
@RequiredArgsConstructor
public class PosController {

    private final PosService posService;

    private String getCurrentEmail(Authentication authentication) {
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Bạn chưa đăng nhập");
        }

        String email = authentication.getName();
        if (email == null || email.isBlank() || "anonymousUser".equalsIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Không xác định được người dùng đăng nhập");
        }

        return email;
    }

    @GetMapping("/products")
    public ResponseEntity<List<PosProductColorGetDto>> searchProducts(
            @RequestParam(required = false, defaultValue = "") String keyword
    ) {
        return ResponseEntity.ok(posService.searchProducts(keyword));
    }

    @GetMapping("/customers")
    public ResponseEntity<List<PosCustomerGetDto>> searchCustomers(
            @RequestParam(required = false, defaultValue = "") String keyword
    ) {
        return ResponseEntity.ok(posService.searchCustomers(keyword));
    }

    @GetMapping("/orders/pending")
    public ResponseEntity<List<PosOrderGetDto>> getPendingOrders(Authentication authentication) {
        return ResponseEntity.ok(posService.getPendingOrders(getCurrentEmail(authentication)));
    }

    @PostMapping("/orders")
    public ResponseEntity<PosOrderGetDto> createOfflineOrder(
            @RequestBody PostOfflineOrderDto dto,
            Authentication authentication
    ) {
        return ResponseEntity.ok(posService.createOfflineOrder(dto, getCurrentEmail(authentication)));
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<PosOrderGetDto> getOrder(
            @PathVariable Integer orderId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(posService.getOrder(orderId, getCurrentEmail(authentication)));
    }

    @DeleteMapping("/orders/{orderId}")
    public ResponseEntity<Void> cancelPendingOrder(
            @PathVariable Integer orderId,
            Authentication authentication
    ) {
        posService.cancelPendingOrder(orderId, getCurrentEmail(authentication));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/orders/{orderId}/items")
    public ResponseEntity<PosOrderGetDto> addItem(
            @PathVariable Integer orderId,
            @RequestBody PostOfflineOrderItemDto dto,
            Authentication authentication
    ) {
        return ResponseEntity.ok(posService.addItem(orderId, dto, getCurrentEmail(authentication)));
    }

    @PutMapping("/orders/{orderId}/items/{orderDetailId}")
    public ResponseEntity<PosOrderGetDto> updateItem(
            @PathVariable Integer orderId,
            @PathVariable Integer orderDetailId,
            @RequestBody PutOfflineOrderItemDto dto,
            Authentication authentication
    ) {
        return ResponseEntity.ok(posService.updateItem(orderId, orderDetailId, dto, getCurrentEmail(authentication)));
    }

    @PutMapping("/orders/{orderId}")
    public ResponseEntity<PosOrderGetDto> updateOrderInfo(
            @PathVariable Integer orderId,
            @RequestBody PutOfflineOrderInfoDto dto,
            Authentication authentication
    ) {
        return ResponseEntity.ok(posService.updateOrderInfo(orderId, dto, getCurrentEmail(authentication)));
    }

    @DeleteMapping("/orders/{orderId}/items/{orderDetailId}")
    public ResponseEntity<PosOrderGetDto> removeItem(
            @PathVariable Integer orderId,
            @PathVariable Integer orderDetailId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(posService.removeItem(orderId, orderDetailId, getCurrentEmail(authentication)));
    }

    @PostMapping("/orders/{orderId}/coupon")
    public ResponseEntity<PosOrderGetDto> applyCoupon(
            @PathVariable Integer orderId,
            @RequestBody PostApplyCouponDto dto,
            Authentication authentication
    ) {
        return ResponseEntity.ok(posService.applyCoupon(orderId, dto, getCurrentEmail(authentication)));
    }

    @GetMapping("/orders/{orderId}/promotions")
    public ResponseEntity<List<PosPromotionGetDto>> getAvailablePromotions(
            @PathVariable Integer orderId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(posService.getAvailablePromotions(orderId, getCurrentEmail(authentication)));
    }

    @PostMapping("/orders/{orderId}/checkout")
    public ResponseEntity<PosOrderGetDto> checkout(
            @PathVariable Integer orderId,
            @RequestBody PostCheckoutOrderDto dto,
            Authentication authentication
    ) {
        return ResponseEntity.ok(posService.checkout(orderId, dto, getCurrentEmail(authentication)));
    }
}