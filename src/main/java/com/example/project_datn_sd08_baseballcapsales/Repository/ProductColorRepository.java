package com.example.project_datn_sd08_baseballcapsales.Repository;

import com.example.project_datn_sd08_baseballcapsales.Model.entity.ProductColor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductColorRepository extends JpaRepository<ProductColor, Integer> {

    List<ProductColor> findByProductID_Id(Integer id);

    List<ProductColor> findByStockQuantityLessThanEqual(Integer stockQuantity);

    List<ProductColor> findByProductID_ProductNameContainingIgnoreCaseOrProductID_BrandID_NameContainingIgnoreCase(
            String productName,
            String brandName
    );

    boolean existsByProductID_IdAndColorID_IdAndSizeID_SizeID(
            Integer productId,
            Integer colorId,
            Integer sizeId
    );

    boolean existsByProductID_IdAndColorID_IdAndSizeID_SizeIDAndIdNot(
            Integer productId,
            Integer colorId,
            Integer sizeId,
            Integer id
    );
}