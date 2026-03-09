package com.example.project_datn_sd08_baseballcapsales.Service;

import com.example.project_datn_sd08_baseballcapsales.Model.dto.ProductDto.ColorDetailDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.ProductDto.ProductCardDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.ProductDto.ProductDetailDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto.GetProductColorDto;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.Image;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.Product;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.ProductColor;
import com.example.project_datn_sd08_baseballcapsales.Repository.ImageRepository;
import com.example.project_datn_sd08_baseballcapsales.Repository.ProductColorRepository;
import com.example.project_datn_sd08_baseballcapsales.Repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductColorService {
    @Autowired
    private ProductColorRepository productColorRepository;
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private ProductRepository productRepository;

    public ProductDetailDto getProductDetail(Integer id){

        Product product = productRepository.findById(id).orElse(null);

        List<ProductColor> productColors =
                productColorRepository.findByProductID_Id(id);

        List<ColorDetailDto> colors = new ArrayList<>();

        for(ProductColor pc : productColors){

            ColorDetailDto colorDto = new ColorDetailDto();

            colorDto.setProductColorID(pc.getId());
            colorDto.setColorName(pc.getColorID().getColorName());
            colorDto.setColorCode(pc.getColorID().getColorCode());

            List<Image> images =
                    imageRepository.findByProductColorID_Id(pc.getId());

            List<String> imageUrls = images
                    .stream()
                    .map(Image::getImageUrl)
                    .toList();

            colorDto.setImages(imageUrls);

            colors.add(colorDto);
        }

        ProductDetailDto dto = new ProductDetailDto();

        dto.setProductID(product.getId());
        dto.setProductName(product.getProductName());
        dto.setPrice(product.getPrice().doubleValue());
        dto.setColors(colors);

        return dto;
    }
    public List<GetProductColorDto> getAllProductColors() {
        return productColorRepository.findAll().stream()
                .map(GetProductColorDto::new)
                .toList();
    }
    public List<ProductCardDto> getProductCards() {
        return productColorRepository.findAll()
                .stream()
                .map(ProductCardDto::new)
                .toList();
    }
}
