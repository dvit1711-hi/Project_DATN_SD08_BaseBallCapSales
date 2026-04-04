package com.example.project_datn_sd08_baseballcapsales.Service;

import com.example.project_datn_sd08_baseballcapsales.Model.dto.PostDto.PostBrandDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.PutDto.PutBrandDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto.GetBranDto;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.Brand;
import com.example.project_datn_sd08_baseballcapsales.Repository.BrandRepository;
import com.example.project_datn_sd08_baseballcapsales.Repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BrandService {

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private ProductRepository productRepository;

    public List<GetBranDto> getAllBrands() {
        return brandRepository.findAll()
                .stream()
                .map(GetBranDto::new)
                .toList();
    }

    public List<GetBranDto> getActiveBrands() {
        return brandRepository.findByStatus("ACTIVE")
                .stream()
                .map(GetBranDto::new)
                .toList();
    }

    public Brand postBrand(PostBrandDto dto) {
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new IllegalArgumentException("Tên brand không được để trống");
        }

        if (brandRepository.existsByNameIgnoreCase(dto.getName().trim())) {
            throw new IllegalArgumentException("Brand đã tồn tại");
        }

        Brand brand = new Brand();
        brand.setName(dto.getName().trim());
        brand.setStatus(dto.getStatus());

        return brandRepository.save(brand);
    }

    public Brand putBrand(Integer id, PutBrandDto dto) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Brand not found"));

        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new IllegalArgumentException("Tên brand không được để trống");
        }

        brand.setName(dto.getName().trim());
        brand.setStatus(dto.getStatus() == null || dto.getStatus().isBlank() ? "ACTIVE" : dto.getStatus().trim());
        return brandRepository.save(brand);
    }

    @Transactional
    public String deleteBrand(Integer id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Brand ID " + id + " not found"));

        boolean used = productRepository.existsByBrandID_BrandID(id);

        if (used) {
            brand.setStatus("INACTIVE");
            brandRepository.save(brand);
            return "Brand đang được sử dụng, đã chuyển sang INACTIVE";
        }

        brandRepository.delete(brand);
        return "Đã xóa brand";
    }
}