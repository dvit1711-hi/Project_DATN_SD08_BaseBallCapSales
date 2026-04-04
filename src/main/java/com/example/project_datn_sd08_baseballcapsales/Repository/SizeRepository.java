package com.example.project_datn_sd08_baseballcapsales.Repository;

import com.example.project_datn_sd08_baseballcapsales.Model.entity.SizeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SizeRepository extends JpaRepository<SizeEntity, Integer> {

    List<SizeEntity> findByStatus(String status);

    Optional<SizeEntity> findBySizeIDAndStatus(Integer sizeID, String status);

    boolean existsBySizeNameIgnoreCase(String sizeName);
}