package com.example.project_datn_sd08_baseballcapsales.Service;

import com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto.GetProductDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.PostDto.PostProductDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.PutDto.PutProductDto;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.Brand;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.Product;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.ProductColor;
import com.example.project_datn_sd08_baseballcapsales.Repository.BrandRepository;
import com.example.project_datn_sd08_baseballcapsales.Repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private BrandRepository brandRepository;

    public List<GetProductDto> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(GetProductDto::new)
                .toList();
    }

    public Product PostProductDto (PostProductDto postProductDto) {
        if(postProductDto.getProductName() == null || postProductDto.getProductName().isBlank())
            throw new IllegalArgumentException("Tên sản phẩm không được để trống");
        if(postProductDto.getPrice() == null || postProductDto.getPrice().compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Giá sản phẩm không hợp lệ");
        if(postProductDto.getBrandID() == null)
            throw new IllegalArgumentException("BrandID không được để trống");
        if(postProductDto.getStatus() == null || (!postProductDto.getStatus().equals("ACTIVE") && !postProductDto.getStatus().equals("INACTIVE")))
            throw new IllegalArgumentException("Status phải là ACTIVE hoặc INACTIVE");

        Brand brand =brandRepository.findById(postProductDto.getBrandID())
                .orElseThrow(() -> new RuntimeException("Brand not found"));

        Product product = new Product();
        product.setProductName( postProductDto.getProductName());
        product.setDescription( postProductDto.getDescription());
        product.setPrice( postProductDto.getPrice());
        product.setStatus( postProductDto.getStatus());
        product.setBrandID( brand);
        return productRepository.save(product);
    }

    public Product PutProductDto (Integer id, PutProductDto putProductDto) {
        Optional<Product> product = productRepository.findById(id);
        if(!product.isPresent()){
            throw new IllegalArgumentException("product id not found");
        }

        Product product1 = product.get();
        product1.setProductName( putProductDto.getProductName());
        product1.setDescription( putProductDto.getDescription());
        product1.setPrice( putProductDto.getPrice());
        product1.setStatus( putProductDto.getStatus());
        Brand brand = brandRepository.findById(putProductDto.getBrandID())
                .orElseThrow(() -> new RuntimeException("Brand not found"));
        product1.setBrandID(brand);
        return productRepository.save(product1);
    }

    public boolean deleteProduct(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product id " + id + " not found"));

        product.setStatus("INACTIVE"); // Hoặc "DELETED" tuỳ convention
        productRepository.save(product);
        return true;
    }
}
