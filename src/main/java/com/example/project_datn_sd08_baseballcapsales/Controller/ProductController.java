package com.example.project_datn_sd08_baseballcapsales.Controller;

import com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto.GetProductDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.PostDto.PostProductDto;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.Product;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.PutDto.PutProductDto;
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

    @GetMapping
    public List<GetProductDto> getProducts() {
        return productService.getAllProducts();
    }

    @PostMapping
    public ResponseEntity<Product> postProduct(@Valid @RequestBody PostProductDto dto){
        Product product =productService.PostProductDto(dto);
        return ResponseEntity.ok(product);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> putDonHang(@PathVariable Integer id,@Valid @RequestBody PutProductDto dto){
        Product product = productService.PutProductDto(id, dto);
        if (product == null) {
            return ResponseEntity
                    .status(404)
                    .body("DonHang Not Found");
        }
        return ResponseEntity.ok(product);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDonHang(@PathVariable Integer id){
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
