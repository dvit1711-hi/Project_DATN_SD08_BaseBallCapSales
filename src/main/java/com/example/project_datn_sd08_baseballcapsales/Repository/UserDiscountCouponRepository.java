package com.example.project_datn_sd08_baseballcapsales.Repository;

import com.example.project_datn_sd08_baseballcapsales.Model.entity.UserDiscountCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserDiscountCouponRepository extends JpaRepository<UserDiscountCoupon, Integer> {
    List<UserDiscountCoupon> findByAccountId(Integer accountId);
    
    List<UserDiscountCoupon> findByAccountIdAndStatus(Integer accountId, String status);
    
    Optional<UserDiscountCoupon> findByAccountIdAndCouponId(Integer accountId, Integer couponId);
    
    @Query("SELECT udc FROM UserDiscountCoupon udc WHERE udc.accountId = :accountId AND udc.status = 'claimed'")
    List<UserDiscountCoupon> findClaimedCouponsByAccountId(@Param("accountId") Integer accountId);
}
