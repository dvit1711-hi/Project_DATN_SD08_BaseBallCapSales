package com.example.project_datn_sd08_baseballcapsales.Service;

import com.example.project_datn_sd08_baseballcapsales.Model.dto.PostDto.PostUserDiscountCouponDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.PutDto.PutUserDiscountCouponDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto.GetUserDiscountCouponDto;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.DiscountCoupon;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.UserDiscountCoupon;
import com.example.project_datn_sd08_baseballcapsales.Repository.DiscountCouponRepository;
import com.example.project_datn_sd08_baseballcapsales.Repository.UserDiscountCouponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class UserDiscountCouponService {

    @Autowired
    private UserDiscountCouponRepository userDiscountCouponRepository;

    @Autowired
    private DiscountCouponRepository discountCouponRepository;

    // Get all user's discount coupons
    public List<GetUserDiscountCouponDto> getUserCoupons(Integer accountId) {
        return userDiscountCouponRepository.findByAccountId(accountId)
                .stream()
                .map(GetUserDiscountCouponDto::new)
                .toList();
    }

    // Get user's claimed coupons
    public List<GetUserDiscountCouponDto> getClaimedCoupons(Integer accountId) {
        return userDiscountCouponRepository.findClaimedCouponsByAccountId(accountId)
                .stream()
                .map(GetUserDiscountCouponDto::new)
                .toList();
    }

    // Get user's coupons by status
    public List<GetUserDiscountCouponDto> getCouponsByStatus(Integer accountId, String status) {
        return userDiscountCouponRepository.findByAccountIdAndStatus(accountId, status)
                .stream()
                .map(GetUserDiscountCouponDto::new)
                .toList();
    }

    // Claim a discount coupon for user
    public GetUserDiscountCouponDto claimCoupon(Integer accountId, PostUserDiscountCouponDto dto) {
        // Validate coupon exists and is active
        DiscountCoupon discountCoupon = discountCouponRepository.findById(dto.getCouponId())
                .orElseThrow(() -> new RuntimeException("Mã giảm giá không tồn tại"));

        if (!discountCoupon.getActive()) {
            throw new RuntimeException("Mã giảm giá đã bị tắt");
        }

        // Check if not expired
        LocalDate now = LocalDate.now();
        if (discountCoupon.getStartDate() != null && now.isBefore(discountCoupon.getStartDate())) {
            throw new RuntimeException("Mã giảm giá chưa đến thời gian áp dụng");
        }
        if (discountCoupon.getEndDate() != null && now.isAfter(discountCoupon.getEndDate())) {
            throw new RuntimeException("Mã giảm giá đã hết hạn");
        }

        // Check if still has available quantity
        if (discountCoupon.getQuantity() != null && discountCoupon.getQuantity() <= 0) {
            throw new RuntimeException("Mã giảm giá đã hết lượt sử dụng");
        }

        // Check if user already claimed this coupon
        Optional<UserDiscountCoupon> existing = userDiscountCouponRepository
                .findByAccountIdAndCouponId(accountId, dto.getCouponId());
        
        if (existing.isPresent() && "claimed".equals(existing.get().getStatus())) {
            throw new RuntimeException("Bạn đã nhận mã giảm giá này rồi");
        }

        // Create new user discount coupon record
        UserDiscountCoupon userDiscountCoupon = new UserDiscountCoupon();
        userDiscountCoupon.setAccountId(accountId);
        userDiscountCoupon.setCouponId(dto.getCouponId());
        userDiscountCoupon.setStatus("claimed");
        userDiscountCoupon.setDiscountCoupon(discountCoupon);

        return new GetUserDiscountCouponDto(userDiscountCouponRepository.save(userDiscountCoupon));
    }

    // Get single user coupon by ID
    public GetUserDiscountCouponDto getUserCouponById(Integer id) {
        return userDiscountCouponRepository.findById(id)
                .map(GetUserDiscountCouponDto::new)
                .orElseThrow(() -> new RuntimeException("Mã giảm giá của bạn không tìm thấy"));
    }

    // Update user coupon status
    public GetUserDiscountCouponDto updateCouponStatus(Integer id, PutUserDiscountCouponDto dto) {
        UserDiscountCoupon userDiscountCoupon = userDiscountCouponRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mã giảm giá của bạn không tìm thấy"));

        if (dto.getStatus() != null) {
            userDiscountCoupon.setStatus(dto.getStatus());
            if ("used".equals(dto.getStatus())) {
                userDiscountCoupon.setUsedDate(Instant.now());
            }
        }

        return new GetUserDiscountCouponDto(userDiscountCouponRepository.save(userDiscountCoupon));
    }

    // Delete user coupon
    public boolean deleteUserCoupon(Integer id) {
        if (!userDiscountCouponRepository.existsById(id)) {
            throw new RuntimeException("Mã giảm giá không tìm thấy");
        }
        userDiscountCouponRepository.deleteById(id);
        return true;
    }

    // Get all available coupons for promotion page
    public List<DiscountCoupon> getAvailableCoupons() {
        LocalDate now = LocalDate.now();
        return discountCouponRepository.findAll()
                .stream()
                .filter(c -> c.getActive() != null && c.getActive())
                .filter(c -> (c.getStartDate() == null || !now.isBefore(c.getStartDate())))
                .filter(c -> (c.getEndDate() == null || !now.isAfter(c.getEndDate())))
                .filter(c -> c.getQuantity() != null && c.getQuantity() > 0)
                .toList();
    }
}
