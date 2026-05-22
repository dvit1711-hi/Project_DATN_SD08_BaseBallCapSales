package com.example.project_datn_sd08_baseballcapsales.Controller;

import com.example.project_datn_sd08_baseballcapsales.Model.dto.PostDto.PostProductColorDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.PutDto.PutProductColorDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto.GetProductColorDto;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.ProductColor;
import com.example.project_datn_sd08_baseballcapsales.Service.ProductColorService;
import com.example.project_datn_sd08_baseballcapsales.payload.reponse.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    /**
     * POST /api/product-color/{productId}/colors/batch
     * Body: [ { colorID, sizeID, price, stockQuantity, status, isRepresentative }, ... ]
     */
    @PostMapping("/{productId}/colors/batch")
    public ResponseEntity<?> createBatch(
            @PathVariable Integer productId,
            @RequestBody List<PostProductColorDto> dtos) {
        try {
            List<ProductColor> result = productColorService.createBatch(productId, dtos);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/{productId}/color")
    public ResponseEntity<?> addProductColor(
            @PathVariable Integer productId,
            @RequestBody PostProductColorDto dto) {
        ProductColor result = productColorService.createProductColor(productId, dto);
        return ResponseEntity.ok(ApiResponse.success("Thêm biến thể thành công", result.getId()));
    }

    @PutMapping("/{productColorId}")
    public ResponseEntity<?> updateProductColor(
            @PathVariable Integer productColorId,
            @RequestBody PutProductColorDto dto) {
        ProductColor result = productColorService.updateProductColor(productColorId, dto);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật biến thể thành công", result.getId()));
    }

    @DeleteMapping("/{productColorId}")
    public ResponseEntity<?> deleteProductColor(@PathVariable Integer productColorId) {
        productColorService.deleteProductColor(productColorId);
        return ResponseEntity.ok(ApiResponse.success("Xóa biến thể thành công"));
    }
}