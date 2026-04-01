package com.example.project_datn_sd08_baseballcapsales.Controller;

import com.example.project_datn_sd08_baseballcapsales.Model.dto.PostDto.PostProductDiscountDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.PutDto.PutProductDiscountDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto.GetProductDiscountDto;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.ProductDiscount;
import com.example.project_datn_sd08_baseballcapsales.Service.ProductDiscountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product-discounts")
public class ProductDiscountController {

    @Autowired
    private ProductDiscountService productDiscountService;

    @GetMapping
    public List<GetProductDiscountDto> getAll() {
        return productDiscountService.getAllDiscounts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDiscountById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(productDiscountService.getDiscountById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @GetMapping("/product-color/{productColorId}")
    public List<GetProductDiscountDto> getDiscountsByProductColor(@PathVariable Integer productColorId) {
        return productDiscountService.getDiscountsByProductColorId(productColorId);
    }

    @GetMapping("/active")
    public List<GetProductDiscountDto> getActiveDiscounts() {
        return productDiscountService.getActiveDiscounts();
    }

    @GetMapping("/reason/{reason}")
    public List<GetProductDiscountDto> getDiscountsByReason(@PathVariable String reason) {
        return productDiscountService.getDiscountsByReason(reason);
    }

    @PostMapping
    public ResponseEntity<ProductDiscount> createDiscount(@Valid @RequestBody PostProductDiscountDto dto) {
        return ResponseEntity.ok(productDiscountService.createDiscount(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateDiscount(@PathVariable Integer id, @Valid @RequestBody PutProductDiscountDto dto) {
        try {
            ProductDiscount updated = productDiscountService.updateDiscount(id, dto);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDiscount(@PathVariable Integer id) {
        try {
            productDiscountService.deleteDiscount(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @GetMapping("/{id}/valid")
    public ResponseEntity<?> isDiscountValid(@PathVariable Integer id) {
        try {
            boolean isValid = productDiscountService.isDiscountValid(id);
            return ResponseEntity.ok(new ValidationResponse(isValid));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @PostMapping("/{id}/use")
    public ResponseEntity<?> useDiscount(@PathVariable Integer id) {
        try {
            productDiscountService.incrementQuantityUsed(id);
            return ResponseEntity.ok("Cập nhật số lượng sử dụng thành công");
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    static class ValidationResponse {
        public boolean isValid;

        public ValidationResponse(boolean isValid) {
            this.isValid = isValid;
        }
    }
}
