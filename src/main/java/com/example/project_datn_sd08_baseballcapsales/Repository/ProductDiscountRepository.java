package com.example.project_datn_sd08_baseballcapsales.Repository;

import com.example.project_datn_sd08_baseballcapsales.Model.entity.ProductDiscount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ProductDiscountRepository extends JpaRepository<ProductDiscount, Integer> {
    List<ProductDiscount> findByProductColorId(Integer productColorId);
    Optional<ProductDiscount> findByProductColorIdAndActive(Integer productColorId, Boolean active);
    List<ProductDiscount> findAllByActive(Boolean active);
    List<ProductDiscount> findAllByReason(String reason);

    @Query("""
        select pd
        from ProductDiscount pd
        where pd.productColor.id = :productColorId
          and (pd.active is null or pd.active = true)
          and (:today >= pd.startDate)
          and (:today <= pd.endDate)
          and (
                pd.quantity is null
                or pd.quantityUsed is null
                or pd.quantityUsed < pd.quantity
              )
        order by pd.createdAt desc
    """)
    List<ProductDiscount> findActiveDiscounts(
            @Param("productColorId") Integer productColorId,
            @Param("today") LocalDate today
    );
}
