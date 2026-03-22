package com.example.project_datn_sd08_baseballcapsales.Controller;

import com.example.project_datn_sd08_baseballcapsales.Model.dto.PostDto.PostColorDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.ProductDto.ProductColorDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto.GetProductColorDto;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.Color;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.Image;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.Product;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.ProductColor;
import com.example.project_datn_sd08_baseballcapsales.Repository.ColorRepository;
import com.example.project_datn_sd08_baseballcapsales.Repository.ImageRepository;
import com.example.project_datn_sd08_baseballcapsales.Repository.ProductColorRepository;
import com.example.project_datn_sd08_baseballcapsales.Repository.ProductRepository;
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
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private ProductColorRepository productColorRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ColorRepository colorRepository;

    @GetMapping("/{id}")
    public ProductColor getById(@PathVariable Integer id){
            return productColorRepository.findById(id).orElse(null);
    }
    @GetMapping
    public List<GetProductColorDto> getAll() {
        return productColorService.getAllProductColors();
    }
    @PostMapping("/{productId}/color")
    public ResponseEntity<ProductColor> addProductColor(
            @PathVariable Integer productId,
            @RequestBody PostColorDto dto) {

        if (productId == null || dto.getColorId() == null) {
            throw new IllegalArgumentException("productId and colorId must not be null");
        }
        if (dto.getColorId() == null)
            throw new IllegalArgumentException("colorId không được để trống");
        if (dto.getStockQuantity() != null && dto.getStockQuantity() < 0)
            throw new IllegalArgumentException("stockQuantity phải >= 0");


        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Color color = colorRepository.findById(dto.getColorId())
                .orElseThrow(() -> new RuntimeException("Color not found"));

        ProductColor pc = new ProductColor();
        pc.setProductID(product);
        pc.setColorID(color);
        pc.setStockQuantity(dto.getStockQuantity() != null ? dto.getStockQuantity() : 0);

        ProductColor saved = productColorRepository.save(pc);
        return ResponseEntity.ok(saved);
    }
    @DeleteMapping("/color/{productcolorId}")
    public ResponseEntity<?> deleteProductColor(@PathVariable Integer productcolorId) {
        ProductColor pc = productColorRepository.findById(productcolorId)
                .orElseThrow(() -> new RuntimeException("Product color not found"));
        List<Image> images = imageRepository.findByProductColorID_Id(productcolorId);
        imageRepository.deleteAll(images);
        productColorRepository.delete(pc);
        return ResponseEntity.noContent().build();
    }

}
