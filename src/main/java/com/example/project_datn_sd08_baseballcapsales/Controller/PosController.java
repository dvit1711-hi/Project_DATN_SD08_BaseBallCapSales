package com.example.project_datn_sd08_baseballcapsales.Controller;

import com.example.project_datn_sd08_baseballcapsales.Model.dto.PostDto.*;
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

    private String getCurrentUsername(Authentication authentication) {
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Bạn chưa đăng nhập");
        }

        String username = authentication.getName();
        if (username == null || username.isBlank() || "anonymousUser".equalsIgnoreCase(username)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Không xác định được người dùng đăng nhập");
        }

        return username;
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

    @PostMapping("/orders")
    public ResponseEntity<PosOrderGetDto> createOfflineOrder(
            @RequestBody PostOfflineOrderDto dto,
            Authentication authentication
    ) {
        return ResponseEntity.ok(posService.createOfflineOrder(dto, getCurrentUsername(authentication)));
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<PosOrderGetDto> getOrder(@PathVariable Integer orderId) {
        return ResponseEntity.ok(posService.getOrder(orderId));
    }

    @PostMapping("/orders/{orderId}/items")
    public ResponseEntity<PosOrderGetDto> addItem(
            @PathVariable Integer orderId,
            @RequestBody PostOfflineOrderItemDto dto
    ) {
            return ResponseEntity.ok(posService.addItem(orderId, dto));
    }

    @PutMapping("/orders/{orderId}/items/{orderDetailId}")
    public ResponseEntity<PosOrderGetDto> updateItem(
            @PathVariable Integer orderId,
            @PathVariable Integer orderDetailId,
            @RequestBody PutOfflineOrderItemDto dto
    ) {
        return ResponseEntity.ok(posService.updateItem(orderId, orderDetailId, dto));
    }

    @DeleteMapping("/orders/{orderId}/items/{orderDetailId}")
    public ResponseEntity<PosOrderGetDto> removeItem(
            @PathVariable Integer orderId,
            @PathVariable Integer orderDetailId
    ) {
        return ResponseEntity.ok(posService.removeItem(orderId, orderDetailId));
    }

    @PostMapping("/orders/{orderId}/coupon")
    public ResponseEntity<PosOrderGetDto> applyCoupon(
            @PathVariable Integer orderId,
            @RequestBody PostApplyCouponDto dto
    ) {
        return ResponseEntity.ok(posService.applyCoupon(orderId, dto));
    }

    @PostMapping("/orders/{orderId}/checkout")
    public ResponseEntity<PosOrderGetDto> checkout(
            @PathVariable Integer orderId,
            @RequestBody PostCheckoutOrderDto dto
    ) {
        return ResponseEntity.ok(posService.checkout(orderId, dto));
    }
}