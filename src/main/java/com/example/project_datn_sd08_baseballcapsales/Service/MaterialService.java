package com.example.project_datn_sd08_baseballcapsales.Service;

import com.example.project_datn_sd08_baseballcapsales.Model.dto.PostDto.PostMaterialDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto.GetMaterialDto;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.Material;
import com.example.project_datn_sd08_baseballcapsales.Repository.MaterialRepository;
import com.example.project_datn_sd08_baseballcapsales.Repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MaterialService {

    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private ProductRepository productRepository;

    public List<GetMaterialDto> getAll() {
        return materialRepository.findAll()
                .stream()
                .map(GetMaterialDto::new)
                .toList();
    }

    public List<GetMaterialDto> getAllActive() {
        return materialRepository.findByStatus("ACTIVE")
                .stream()
                .map(GetMaterialDto::new)
                .toList();
    }

    public Material getById(Integer id) {
        return materialRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Material not found"));
    }

    public Material create(PostMaterialDto dto) {
        if (dto.getMaterialName() == null || dto.getMaterialName().isBlank()) {
            throw new IllegalArgumentException("Tên chất liệu không được để trống");
        }

        if (materialRepository.existsByMaterialNameIgnoreCase(dto.getMaterialName().trim())) {
            throw new IllegalArgumentException("Chất liệu đã tồn tại");
        }

        Material material = new Material();
        material.setMaterialName(dto.getMaterialName().trim());
        material.setStatus(dto.getStatus() == null || dto.getStatus().isBlank() ? "ACTIVE" : dto.getStatus().trim());

        return materialRepository.save(material);
    }

    public Material update(Integer id, PostMaterialDto dto) {
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Material not found"));

        if (dto.getMaterialName() == null || dto.getMaterialName().isBlank()) {
            throw new IllegalArgumentException("Tên chất liệu không được để trống");
        }

        material.setMaterialName(dto.getMaterialName().trim());
        material.setStatus(dto.getStatus().trim());
        return materialRepository.save(material);
    }

    @Transactional
    public String delete(Integer id) {
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Material not found"));

        boolean used = productRepository.existsByMaterialID_MaterialID(id);

        if (used) {
            material.setStatus("INACTIVE");
            materialRepository.save(material);
            return "Material đang được sử dụng, đã chuyển sang INACTIVE";
        }

        materialRepository.delete(material);
        return "Đã xóa material";
    }
}