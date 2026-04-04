package com.example.project_datn_sd08_baseballcapsales.Controller;

import com.example.project_datn_sd08_baseballcapsales.Model.dto.PostDto.PostMaterialDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto.GetMaterialDto;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.Material;
import com.example.project_datn_sd08_baseballcapsales.Service.MaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/material")
public class MaterialController {

    @Autowired
    private MaterialService materialService;

    @GetMapping
    public List<GetMaterialDto> getAll() {
        return materialService.getAll();
    }

    @GetMapping("/active")
    public List<GetMaterialDto> getAllActive() {
        return materialService.getAllActive();
    }

    @GetMapping("/{id}")
    public Material getById(@PathVariable Integer id) {
        return materialService.getById(id);
    }

    @PostMapping
    public ResponseEntity<Material> create(@RequestBody PostMaterialDto dto) {
        return ResponseEntity.ok(materialService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Material> update(@PathVariable Integer id,
                                           @RequestBody PostMaterialDto dto) {
        return ResponseEntity.ok(materialService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        return ResponseEntity.ok(materialService.delete(id));
    }
}