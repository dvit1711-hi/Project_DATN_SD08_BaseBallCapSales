package com.example.project_datn_sd08_baseballcapsales.Controller;

import com.example.project_datn_sd08_baseballcapsales.Model.dto.*;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.DiscountCoupon;
import com.example.project_datn_sd08_baseballcapsales.Service.DiscountCouponService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/discountpon")
public class DiscountCouponController {

    @Autowired
    private DiscountCouponService discountCouponService;

    @GetMapping
    public List<GetDiscountCouponDto> getAll() {
        return discountCouponService.getAllCoupons();
    }

    @PostMapping
    public ResponseEntity<DiscountCoupon> postCoupon(@Valid @RequestBody PostDiscountCouponDto dto) {
        return ResponseEntity.ok(discountCouponService.postdiscount(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> putCoupon(@PathVariable Integer id, @Valid @RequestBody PutDiscountCouponDto dto) {
        DiscountCoupon cp = discountCouponService.putCoupon(id, dto);
        if (cp == null) {
            return ResponseEntity.status(404).body("Coupon Not Found");
        }
        return ResponseEntity.ok(cp);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCoupon(@PathVariable Integer id) {
        discountCouponService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
