package com.example.project_datn_sd08_baseballcapsales.Repository;

import com.example.project_datn_sd08_baseballcapsales.Model.entity.DiscountCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DiscountCouponRepository extends JpaRepository<DiscountCoupon, Integer> {
    Optional<DiscountCoupon> findByCouponCodeIgnoreCase(String couponCode);

    @Query("""
        select c
        from DiscountCoupon c
        where (c.active = true or c.active is null)
          and (c.startDate is null or c.startDate <= :today)
          and (c.endDate is null or c.endDate >= :today)
          and (c.quantity is null or c.quantity > 0)
    """)
    List<DiscountCoupon> findAvailableForPos(@Param("today") LocalDate today);
}
