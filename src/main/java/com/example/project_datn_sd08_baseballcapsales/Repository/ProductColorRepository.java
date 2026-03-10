package com.example.project_datn_sd08_baseballcapsales.Repository;

import com.example.project_datn_sd08_baseballcapsales.Model.entity.Product;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.ProductColor;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductColorRepository extends JpaRepository<ProductColor, Integer> {
    List<ProductColor> findByProductID_Id(Integer id);
}
