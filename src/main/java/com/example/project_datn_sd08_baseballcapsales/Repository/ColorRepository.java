package com.example.project_datn_sd08_baseballcapsales.Repository;

import com.example.project_datn_sd08_baseballcapsales.Model.entity.Color;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ColorRepository extends JpaRepository<Color, Integer> {

    List<Color> findByStatus(String status);

    Optional<Color> findByIdAndStatus(Integer id, String status);

    boolean existsByColorNameIgnoreCase(String colorName);

    boolean existsByColorCodeIgnoreCase(String colorCode);
}