package com.example.project_datn_sd08_baseballcapsales.Controller;

import com.example.project_datn_sd08_baseballcapsales.Model.dto.PostDto.PostProductColorDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.PutDto.PutProductColorDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto.GetProductColorDto;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.ProductColor;
import com.example.project_datn_sd08_baseballcapsales.Service.ProductColorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product-color")
public class ProductColorController {

    @Autowired
    private ProductColorService productColorService;

    @GetMapping("/{id}")
    public ProductColor getById(@PathVariable Integer id) {
        return productColorService.getById(id);
    }

    @GetMapping
    public List<GetProductColorDto> getAll() {
        return productColorService.getAllProductColors();
    }

    @PostMapping("/{productId}/color")
    public ResponseEntity<ProductColor> addProductColor(
            @PathVariable Integer productId,
            @RequestBody PostProductColorDto dto) {
        return ResponseEntity.ok(productColorService.createProductColor(productId, dto));
    }

    @PutMapping("/{productColorId}")
    public ResponseEntity<ProductColor> updateProductColor(
            @PathVariable Integer productColorId,
            @RequestBody PutProductColorDto dto) {
        return ResponseEntity.ok(productColorService.updateProductColor(productColorId, dto));
    }

    @DeleteMapping("/{productColorId}")
    public ResponseEntity<Void> deleteProductColor(@PathVariable Integer productColorId) {
        productColorService.deleteProductColor(productColorId);
        return ResponseEntity.noContent().build();
    }
}