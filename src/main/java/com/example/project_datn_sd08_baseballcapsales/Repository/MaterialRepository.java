package com.example.project_datn_sd08_baseballcapsales.Repository;

import com.example.project_datn_sd08_baseballcapsales.Model.entity.Material;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MaterialRepository extends JpaRepository<Material, Integer> {

    List<Material> findByStatus(String status);

    Optional<Material> findByMaterialIDAndStatus(Integer materialID, String status);

    boolean existsByMaterialNameIgnoreCase(String materialName);
}