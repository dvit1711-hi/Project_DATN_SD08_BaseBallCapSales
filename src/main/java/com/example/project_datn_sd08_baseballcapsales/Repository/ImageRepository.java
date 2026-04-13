package com.example.project_datn_sd08_baseballcapsales.Repository;

import com.example.project_datn_sd08_baseballcapsales.Model.entity.Image;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.ProductColor;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface ImageRepository extends JpaRepository<Image, Integer> {
    List<Image> findByProductColorID_Id(Integer id);

    boolean existsByProductColorIDAndIsMainTrue(ProductColor productColor);

    boolean existsByProductColorID_Id(Integer productColorId);

    long deleteByProductColorID_Id(Integer productColorId);

    Optional<Image> findByProductColorID_IdAndIsMainTrue(Integer productColorId);

    List<Image> findByProductColorID(ProductColor productColor);


}
