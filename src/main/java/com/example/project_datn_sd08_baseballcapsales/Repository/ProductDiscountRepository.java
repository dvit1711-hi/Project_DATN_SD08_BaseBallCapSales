package com.example.project_datn_sd08_baseballcapsales.Repository;

import com.example.project_datn_sd08_baseballcapsales.Model.entity.ProductDiscount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductDiscountRepository extends JpaRepository<ProductDiscount, Integer> {
    List<ProductDiscount> findByProductColorId(Integer productColorId);
    Optional<ProductDiscount> findByProductColorIdAndActive(Integer productColorId, Boolean active);
    List<ProductDiscount> findAllByActive(Boolean active);
    List<ProductDiscount> findAllByReason(String reason);
}
