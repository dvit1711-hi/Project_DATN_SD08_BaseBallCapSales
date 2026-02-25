package com.example.project_datn_sd08_baseballcapsales.Service;

import com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto.GetProductColorDto;
import com.example.project_datn_sd08_baseballcapsales.Repository.ProductColorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductColorService {
    @Autowired
    private ProductColorRepository productColorRepository;

    public List<GetProductColorDto> getAllProductColors() {
        return productColorRepository.findAll().stream()
                .map(GetProductColorDto::new)
                .toList();
    }
}
