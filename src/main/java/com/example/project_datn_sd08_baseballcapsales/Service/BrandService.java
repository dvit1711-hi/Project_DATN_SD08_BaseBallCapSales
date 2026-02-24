package com.example.project_datn_sd08_baseballcapsales.Service;

import com.example.project_datn_sd08_baseballcapsales.Model.dto.*;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.Brand;
import com.example.project_datn_sd08_baseballcapsales.Repository.BrandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BrandService {

    @Autowired
    private BrandRepository brandRepository;

    public List<GetBranDto> getAllBrands() {
        return brandRepository.findAll()
                .stream()
                .map(GetBranDto::new)
                .toList();
    }

    public Brand postBrand(PostBrandDto dto) {
        Brand brand = new Brand();
        brand.setName(dto.getName());
        return brandRepository.save(brand);
    }

    public Brand putBrand(Integer id, PutBrandDto dto) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Brand not found"));

        brand.setName(dto.getName());
        return brandRepository.save(brand);
    }

    public boolean deleteBrand(Integer id) {
        if (!brandRepository.existsById(id)) {
            throw new RuntimeException("Brand ID " + id + " not found");
        }
        brandRepository.deleteById(id);
        return true;
    }
}
