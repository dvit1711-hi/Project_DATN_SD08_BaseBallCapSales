package com.example.project_datn_sd08_baseballcapsales.Controller;

import com.example.project_datn_sd08_baseballcapsales.Model.dto.PostDto.PostUserDiscountCouponDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.PutDto.PutUserDiscountCouponDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto.GetDiscountCouponDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto.GetUserDiscountCouponDto;
import com.example.project_datn_sd08_baseballcapsales.Service.UserDiscountCouponService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-discount-coupons")
public class UserDiscountCouponController {

    @Autowired
    private UserDiscountCouponService userDiscountCouponService;

    // Get all user's discount coupons
    @GetMapping("/my-coupons")
    public ResponseEntity<?> getUserCoupons(@RequestParam Integer accountId) {
        try {
            return ResponseEntity.ok(userDiscountCouponService.getUserCoupons(accountId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    // Get user's claimed coupons only
    @GetMapping("/my-claimed")
    public ResponseEntity<?> getClaimedCoupons(@RequestParam Integer accountId) {
        try {
            return ResponseEntity.ok(userDiscountCouponService.getClaimedCoupons(accountId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    // Get user's coupons by status
    @GetMapping("/my-status")
    public ResponseEntity<?> getCouponsByStatus(
            @RequestParam Integer accountId,
            @RequestParam String status) {
        try {
            return ResponseEntity.ok(userDiscountCouponService.getCouponsByStatus(accountId, status));
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    // Get all available coupons for promotion page
    @GetMapping("/available")
    public List<GetDiscountCouponDto> getAvailableCoupons() {
        return userDiscountCouponService.getAvailableCoupons()
                .stream()
                .map(GetDiscountCouponDto::new)
                .toList();
    }

    // Claim a discount coupon
    @PostMapping("/claim")
    public ResponseEntity<?> claimCoupon(
            @RequestParam Integer accountId,
            @Valid @RequestBody PostUserDiscountCouponDto dto) {
        try {
            return ResponseEntity.ok(userDiscountCouponService.claimCoupon(accountId, dto));
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    // Get single user coupon
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserCouponById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(userDiscountCouponService.getUserCouponById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    // Update user coupon status
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCouponStatus(
            @PathVariable Integer id,
            @Valid @RequestBody PutUserDiscountCouponDto dto) {
        try {
            return ResponseEntity.ok(userDiscountCouponService.updateCouponStatus(id, dto));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    // Delete user coupon
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUserCoupon(@PathVariable Integer id) {
        try {
            userDiscountCouponService.deleteUserCoupon(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
}
