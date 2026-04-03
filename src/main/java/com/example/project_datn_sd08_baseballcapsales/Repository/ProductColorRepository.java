package com.example.project_datn_sd08_baseballcapsales.Repository;

import com.example.project_datn_sd08_baseballcapsales.Model.entity.ProductColor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    @Query("""
        select pc
        from ProductColor pc
        join pc.productID p
        join pc.colorID c
        join pc.sizeID s
        where pc.stockQuantity > 0
          and (
                :keyword is null or :keyword = ''
                or lower(p.productName) like lower(concat('%', :keyword, '%'))
                or lower(c.colorName) like lower(concat('%', :keyword, '%'))
                or lower(s.sizeName) like lower(concat('%', :keyword, '%'))
          )
        order by p.productName asc, c.colorName asc, s.sizeName asc
    """)
    List<ProductColor> searchForPos(@Param("keyword") String keyword);
}