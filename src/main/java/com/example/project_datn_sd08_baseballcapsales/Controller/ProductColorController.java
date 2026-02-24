package com.example.project_datn_sd08_baseballcapsales.Controller;

import com.example.project_datn_sd08_baseballcapsales.Model.dto.*;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.ProductColor;
import com.example.project_datn_sd08_baseballcapsales.Service.ProductColorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product-color")
public class ProductColorController {

    @Autowired
    private ProductColorService productColorService;

    @GetMapping
    public List<GetProductColorDto> getAll() {
        return productColorService.getAllProductColors();
    }

}
