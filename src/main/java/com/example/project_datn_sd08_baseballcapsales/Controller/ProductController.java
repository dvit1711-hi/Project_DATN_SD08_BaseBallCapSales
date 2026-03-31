package com.example.project_datn_sd08_baseballcapsales.Controller;

import com.example.project_datn_sd08_baseballcapsales.Model.dto.PostDto.PostProductDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.ProductDto.ProductCardDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.ProductDto.ProductDetailDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.PutDto.PutProductDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto.GetProductDto;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.Product;
import com.example.project_datn_sd08_baseballcapsales.Service.ProductColorService;
import com.example.project_datn_sd08_baseballcapsales.Service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductColorService productColorService;

    @GetMapping("/card")
    public List<ProductCardDto> getProductCards(
            @RequestParam(value = "search", required = false) String search
    ) {
        if (search != null && !search.trim().isEmpty()) {
            return productColorService.searchProductCards(search);
        }
        return productColorService.getProductCards();
    }

    @GetMapping
    public List<GetProductDto> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/detail/{id}")
    public ProductDetailDto getProductDetail(@PathVariable Integer id) {
        return productColorService.getProductDetail(id);
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@Valid @RequestBody PostProductDto dto) {
        return ResponseEntity.ok(productService.createProduct(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Integer id,
                                                 @Valid @RequestBody PutProductDto dto) {
        return ResponseEntity.ok(productService.updateProduct(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Integer id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok("Product status changed to INACTIVE");
    }
}