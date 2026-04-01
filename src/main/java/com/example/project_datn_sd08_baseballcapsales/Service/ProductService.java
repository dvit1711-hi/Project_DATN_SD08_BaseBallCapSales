package com.example.project_datn_sd08_baseballcapsales.Service;

import com.example.project_datn_sd08_baseballcapsales.Model.dto.PostDto.PostProductDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.PutDto.PutProductDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto.GetProductDto;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.Brand;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.Material;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.Product;
import com.example.project_datn_sd08_baseballcapsales.Repository.BrandRepository;
import com.example.project_datn_sd08_baseballcapsales.Repository.MaterialRepository;
import com.example.project_datn_sd08_baseballcapsales.Repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private MaterialRepository materialRepository;

    public List<GetProductDto> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(GetProductDto::new)
                .toList();
    }

    public Product createProduct(PostProductDto dto) {
        if (dto.getProductName() == null || dto.getProductName().isBlank()) {
            throw new IllegalArgumentException("Tên sản phẩm không được để trống");
        }

        if (dto.getBrandID() == null) {
            throw new IllegalArgumentException("BrandID không được để trống");
        }

        if (dto.getMaterialID() == null) {
            throw new IllegalArgumentException("MaterialID không được để trống");
        }

        if (dto.getStatus() == null ||
                (!dto.getStatus().equals("ACTIVE") && !dto.getStatus().equals("INACTIVE"))) {
            throw new IllegalArgumentException("Status phải là ACTIVE hoặc INACTIVE");
        }

        Brand brand = brandRepository.findById(dto.getBrandID())
                .orElseThrow(() -> new RuntimeException("Brand not found"));

        Material material = materialRepository.findById(dto.getMaterialID())
                .orElseThrow(() -> new RuntimeException("Material not found"));

        Product product = new Product();
        product.setProductName(dto.getProductName());
        product.setDescription(dto.getDescription());
        product.setStatus(dto.getStatus());
        product.setBrandID(brand);
        product.setMaterialID(material);

        return productRepository.save(product);
    }

    public Product updateProduct(Integer id, PutProductDto dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product id not found"));

        if (dto.getProductName() == null || dto.getProductName().isBlank()) {
            throw new IllegalArgumentException("Tên sản phẩm không được để trống");
        }

        if (dto.getBrandID() == null) {
            throw new IllegalArgumentException("BrandID không được để trống");
        }

        if (dto.getMaterialID() == null) {
            throw new IllegalArgumentException("MaterialID không được để trống");
        }

        if (dto.getStatus() == null ||
                (!dto.getStatus().equals("ACTIVE") && !dto.getStatus().equals("INACTIVE"))) {
            throw new IllegalArgumentException("Status phải là ACTIVE hoặc INACTIVE");
        }

        Brand brand = brandRepository.findById(dto.getBrandID())
                .orElseThrow(() -> new RuntimeException("Brand not found"));

        Material material = materialRepository.findById(dto.getMaterialID())
                .orElseThrow(() -> new RuntimeException("Material not found"));

        product.setProductName(dto.getProductName());
        product.setDescription(dto.getDescription());
        product.setStatus(dto.getStatus());
        product.setBrandID(brand);
        product.setMaterialID(material);

        return productRepository.save(product);
    }

    public boolean deleteProduct(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product id " + id + " not found"));

        product.setStatus("INACTIVE");
        productRepository.save(product);
        return true;
    }

    public Product getProductEntityById(Integer id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
    }
}