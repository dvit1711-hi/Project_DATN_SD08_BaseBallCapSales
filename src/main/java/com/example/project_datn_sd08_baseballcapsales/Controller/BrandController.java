package com.example.project_datn_sd08_baseballcapsales.Controller;

import com.example.project_datn_sd08_baseballcapsales.Model.dto.PostDto.PostBrandDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.PutDto.PutBrandDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto.GetBranDto;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.Brand;
import com.example.project_datn_sd08_baseballcapsales.Service.BrandService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/brands")
public class BrandController {

    @Autowired
    private BrandService brandService;

    @GetMapping
    public List<GetBranDto> getBrands() {
        return brandService.getAllBrands();
    }

    @PostMapping
    public ResponseEntity<Brand> createBrand(@Valid @RequestBody PostBrandDto dto) {
        return ResponseEntity.ok(brandService.postBrand(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Brand> updateBrand(@PathVariable Integer id,
                                             @Valid @RequestBody PutBrandDto dto) {
        return ResponseEntity.ok(brandService.putBrand(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBrand(@PathVariable Integer id) {
        brandService.deleteBrand(id);
        return ResponseEntity.noContent().build();
    }
}
