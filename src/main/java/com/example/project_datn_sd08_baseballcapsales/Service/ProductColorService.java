package com.example.project_datn_sd08_baseballcapsales.Service;

import com.example.project_datn_sd08_baseballcapsales.Model.dto.PostDto.PostProductColorDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.ProductDto.ColorDetailDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.ProductDto.ProductCardDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.ProductDto.ProductDetailDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.PutDto.PutProductColorDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto.GetProductColorDto;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.Color;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.Image;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.Product;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.ProductColor;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.SizeEntity;
import com.example.project_datn_sd08_baseballcapsales.Repository.*;
import com.example.project_datn_sd08_baseballcapsales.Repository.SizeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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

    @Autowired
    private ColorRepository colorRepository;

    @Autowired
    private SizeRepository sizeRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    public ProductDetailDto getProductDetail(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        List<ProductColor> productColors = productColorRepository.findByProductID_Id(id);

        List<ColorDetailDto> colors = new ArrayList<>();

        for (ProductColor pc : productColors) {
            ColorDetailDto colorDto = new ColorDetailDto();
            colorDto.setColorID(pc.getColorID().getId());
            colorDto.setProductColorID(pc.getId());
            colorDto.setColorName(pc.getColorID().getColorName());
            colorDto.setColorCode(pc.getColorID().getColorCode());
            colorDto.setStockQuantity(pc.getStockQuantity());
            colorDto.setPrice(pc.getPrice());
            pc.getStatus();
            colorDto.setSizeID(pc.getSizeID() != null ? pc.getSizeID().getSizeID() : null);
            colorDto.setSizeName(pc.getSizeID() != null ? pc.getSizeID().getSizeName() : null);

            List<Image> images = imageRepository.findByProductColorID_Id(pc.getId());

            List<String> imageUrls = images.stream()
                    .map(Image::getImageUrl)
                    .toList();

            colorDto.setImages(imageUrls);

            colors.add(colorDto);
        }

        ProductDetailDto dto = new ProductDetailDto();
        dto.setProductID(product.getId());
        dto.setProductName(product.getProductName());
        dto.setDescription(product.getDescription());
        dto.setStatus(product.getStatus());

        if (product.getBrandID() != null) {
            dto.setBrandID(product.getBrandID().getBrandID());
            dto.setBrandName(product.getBrandID().getName());
        }

        if (product.getMaterialID() != null) {
            dto.setMaterialID(product.getMaterialID().getMaterialID());
            dto.setMaterialName(product.getMaterialID().getMaterialName());
        }

        dto.setColors(colors);

        return dto;
    }

    public List<GetProductColorDto> getAllProductColors() {
        return productColorRepository.findAll().stream()
                .map(GetProductColorDto::new)
                .toList();
    }

    public ProductColor getById(Integer id) {
        return productColorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product color not found"));
    }

    public ProductColor createProductColor(Integer productId, PostProductColorDto dto) {
        if (productId == null) {
            throw new IllegalArgumentException("productId must not be null");
        }

        if (dto.getColorID() == null) {
            throw new IllegalArgumentException("colorId không được để trống");
        }

        if (dto.getSizeID() == null) {
            throw new IllegalArgumentException("sizeId không được để trống");
        }

        if (dto.getPrice() == null || dto.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("price phải >= 0");
        }

        if (dto.getStockQuantity() != null && dto.getStockQuantity() < 0) {
            throw new IllegalArgumentException("stockQuantity phải >= 0");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Color color = colorRepository.findById(dto.getColorID())
                .orElseThrow(() -> new RuntimeException("Color not found"));

        SizeEntity size = sizeRepository.findById(dto.getSizeID())
                .orElseThrow(() -> new RuntimeException("Size not found"));

        boolean existed = productColorRepository
                .existsByProductID_IdAndColorID_IdAndSizeID_SizeID(
                        productId,
                        dto.getColorID(),
                        dto.getSizeID()
                );

        if (existed) {
            throw new IllegalArgumentException("Biến thể product + color + size đã tồn tại");
        }

        ProductColor pc = new ProductColor();
        pc.setProductID(product);
        pc.setColorID(color);
        pc.setSizeID(size);
        pc.setPrice(dto.getPrice());
        pc.setStockQuantity(dto.getStockQuantity() != null ? dto.getStockQuantity() : 0);
        pc.setStatus(dto.getStatus() == null || dto.getStatus().isBlank() ? "ACTIVE" : dto.getStatus().trim());

        return productColorRepository.save(pc);
    }

    public ProductColor updateProductColor(Integer productColorId, PutProductColorDto dto) {
        ProductColor pc = productColorRepository.findById(productColorId)
                .orElseThrow(() -> new RuntimeException("Product color not found"));

        if (dto.getColorID() == null) {
            throw new IllegalArgumentException("colorId không được để trống");
        }

        if (dto.getSizeID() == null) {
            throw new IllegalArgumentException("sizeId không được để trống");
        }

        if (dto.getPrice() == null || dto.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("price phải >= 0");
        }

        if (dto.getStockQuantity() != null && dto.getStockQuantity() < 0) {
            throw new IllegalArgumentException("stockQuantity phải >= 0");
        }

        Color color = colorRepository.findById(dto.getColorID())
                .orElseThrow(() -> new RuntimeException("Color not found"));

        SizeEntity size = sizeRepository.findById(dto.getSizeID())
                .orElseThrow(() -> new RuntimeException("Size not found"));

        boolean existed = productColorRepository
                .existsByProductID_IdAndColorID_IdAndSizeID_SizeIDAndIdNot(
                        pc.getProductID().getId(),
                        dto.getColorID(),
                        dto.getSizeID(),
                        productColorId
                );

        if (existed) {
            throw new IllegalArgumentException("Biến thể product + color + size đã tồn tại");
        }

        pc.setColorID(color);
        pc.setSizeID(size);
        pc.setPrice(dto.getPrice());
        pc.setStatus(dto.getStatus());
        pc.setStockQuantity(dto.getStockQuantity() != null ? dto.getStockQuantity() : 0);

        return productColorRepository.save(pc);
    }

    @Transactional
    public String deleteProductColor(Integer productColorId) {
        ProductColor pc = productColorRepository.findById(productColorId)
                .orElseThrow(() -> new RuntimeException("Product color not found"));

        boolean usedInOrder = orderDetailRepository.existsByProductColorID_Id(productColorId);
        boolean usedInCart = cartItemRepository.existsByProductColorID_Id(productColorId);

        if (usedInOrder || usedInCart) {
            pc.setStatus("INACTIVE");
            pc.setStockQuantity(0);
            productColorRepository.save(pc);
            return "Biến thể đã phát sinh dữ liệu, đã chuyển sang INACTIVE";
        }

        List<Image> images = imageRepository.findByProductColorID_Id(productColorId);
        imageRepository.deleteAll(images);
        productColorRepository.delete(pc);
        return "Đã xóa cứng biến thể";
    }

    public List<ProductCardDto> getProductCards() {
        return productColorRepository.findAll()
                .stream()
                .map(ProductCardDto::new)
                .filter(dto -> "ACTIVE".equals(dto.getStatus()))
                .toList();
    }

    public List<ProductCardDto> searchProductCards(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getProductCards();
        }

        return productColorRepository
                .findByProductID_ProductNameContainingIgnoreCaseOrProductID_BrandID_NameContainingIgnoreCase(
                        keyword.trim(),
                        keyword.trim()
                )
                .stream()
                .map(ProductCardDto::new)
                .filter(dto -> "ACTIVE".equals(dto.getStatus()))
                .toList();
    }
}