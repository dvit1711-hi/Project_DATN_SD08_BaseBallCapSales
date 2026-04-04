package com.example.project_datn_sd08_baseballcapsales.Repository;

import com.example.project_datn_sd08_baseballcapsales.Model.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BrandRepository extends JpaRepository<Brand, Integer> {

    List<Brand> findByStatus(String status);

    Optional<Brand> findByBrandIDAndStatus(Integer brandID, String status);

    boolean existsByNameIgnoreCase(String name);
}