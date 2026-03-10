package com.example.project_datn_sd08_baseballcapsales.Controller;

import com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto.GetProductColorDto;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.Image;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.ProductColor;
import com.example.project_datn_sd08_baseballcapsales.Repository.ImageRepository;
import com.example.project_datn_sd08_baseballcapsales.Repository.ProductColorRepository;
import com.example.project_datn_sd08_baseballcapsales.Service.ProductColorService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping("/{id}")
    public ProductColor getById(@PathVariable Integer id){
            return productColorRepository.findById(id).orElse(null);
    }
    @GetMapping
    public List<GetProductColorDto> getAll() {
        return productColorService.getAllProductColors();
    }

}
