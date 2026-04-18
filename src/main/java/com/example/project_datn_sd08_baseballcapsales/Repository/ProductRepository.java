package com.example.project_datn_sd08_baseballcapsales.Repository;

import com.example.project_datn_sd08_baseballcapsales.Model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    List<Product> findByStatus(String status);

    Optional<Product> findByIdAndStatus(Integer id, String status);

    List<Product> findByBrandID_BrandID(Integer brandId);

    List<Product> findByBrandID_BrandIDAndStatus(Integer brandId, String status);

    List<Product> findByMaterialID_MaterialID(Integer materialId);

    List<Product> findByMaterialID_MaterialIDAndStatus(Integer materialId, String status);

    boolean existsByBrandID_BrandID(Integer brandId);

    boolean existsByMaterialID_MaterialID(Integer materialId);

    List<Product> findByProductNameContainingIgnoreCase(String keyword);

    List<Product> findByProductNameContainingIgnoreCaseAndStatus(String keyword, String status);

    @Query("""
        select distinct p
        from Product p
        left join fetch p.brandID b
        left join fetch p.materialID m
        left join fetch p.productColors pc
        left join fetch pc.colorID c
        left join fetch pc.sizeID s
        where upper(coalesce(p.status, 'INACTIVE')) = 'ACTIVE'
          and (
                :keyword is null or trim(:keyword) = ''
                or lower(p.productName) like lower(concat('%', :keyword, '%'))
                or lower(b.name) like lower(concat('%', :keyword, '%'))
                or lower(c.colorName) like lower(concat('%', :keyword, '%'))
                or lower(s.sizeName) like lower(concat('%', :keyword, '%'))
              )
        order by p.id desc
    """)
    List<Product> findActiveProductsForStorefront(@Param("keyword") String keyword);
}