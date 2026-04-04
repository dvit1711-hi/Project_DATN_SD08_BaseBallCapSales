package com.example.project_datn_sd08_baseballcapsales.Repository;

import com.example.project_datn_sd08_baseballcapsales.Model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

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
}