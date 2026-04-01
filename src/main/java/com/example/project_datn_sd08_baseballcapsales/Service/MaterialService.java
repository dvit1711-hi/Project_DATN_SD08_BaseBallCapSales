package com.example.project_datn_sd08_baseballcapsales.Service;

import com.example.project_datn_sd08_baseballcapsales.Model.dto.PostDto.PostMaterialDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto.GetMaterialDto;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.Material;
import com.example.project_datn_sd08_baseballcapsales.Repository.MaterialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MaterialService {

    @Autowired
    private MaterialRepository materialRepository;

    public List<GetMaterialDto> getAll() {
        return materialRepository.findAll()
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

        Material material = new Material();
        material.setMaterialName(dto.getMaterialName());

        return materialRepository.save(material);
    }

    public Material update(Integer id, PostMaterialDto dto) {
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Material not found"));

        if (dto.getMaterialName() == null || dto.getMaterialName().isBlank()) {
            throw new IllegalArgumentException("Tên chất liệu không được để trống");
        }

        material.setMaterialName(dto.getMaterialName());
        return materialRepository.save(material);
    }

    public void delete(Integer id) {
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Material not found"));
        materialRepository.delete(material);
    }
}