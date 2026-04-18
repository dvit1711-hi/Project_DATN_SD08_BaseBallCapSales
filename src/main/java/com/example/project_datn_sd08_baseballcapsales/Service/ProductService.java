package com.example.project_datn_sd08_baseballcapsales.Service;

import com.example.project_datn_sd08_baseballcapsales.Model.dto.PostDto.PostProductDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.ProductDto.ProductSummaryCardDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.PutDto.PutProductDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto.GetProductDto;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.Brand;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.Image;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.Material;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.Product;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.ProductColor;
import com.example.project_datn_sd08_baseballcapsales.Repository.BrandRepository;
import com.example.project_datn_sd08_baseballcapsales.Repository.MaterialRepository;
import com.example.project_datn_sd08_baseballcapsales.Repository.ProductColorRepository;
import com.example.project_datn_sd08_baseballcapsales.Repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private ProductColorRepository productColorRepository;

    public List<GetProductDto> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(product -> {
                    GetProductDto dto = new GetProductDto(product);
                    dto.setVariantCount((int) productColorRepository.countByProductID_Id(product.getId()));
                    return dto;
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductSummaryCardDto> getStorefrontProductCards(String keyword) {
        String normalizedKeyword = keyword == null ? null : keyword.trim();

        return productRepository.findActiveProductsForStorefront(normalizedKeyword)
                .stream()
                .map(this::toProductSummaryCard)
                .filter(Objects::nonNull)
                .toList();
    }

    private ProductSummaryCardDto toProductSummaryCard(Product product) {
        if (product == null) {
            return null;
        }

        List<ProductColor> activeVariants = product.getProductColors() == null
                ? new ArrayList<>()
                : product.getProductColors().stream()
                .filter(Objects::nonNull)
                .filter(pc -> "ACTIVE".equalsIgnoreCase(safe(pc.getStatus())))
                .toList();

        if (activeVariants.isEmpty()) {
            return null;
        }

        ProductColor defaultVariant = pickDefaultVariant(activeVariants);

        BigDecimal displayPrice = defaultVariant != null && defaultVariant.getPrice() != null
                ? defaultVariant.getPrice()
                : BigDecimal.ZERO;

        ProductSummaryCardDto dto = new ProductSummaryCardDto();
        dto.setProductID(product.getId());
        dto.setProductName(product.getProductName());
        dto.setStatus(product.getStatus());

        if (product.getBrandID() != null) {
            dto.setBrandID(product.getBrandID().getBrandID());
            dto.setBrandName(product.getBrandID().getName());
        }

        if (product.getMaterialID() != null) {
            dto.setMaterialID(product.getMaterialID().getMaterialID());
            dto.setMaterialName(product.getMaterialID().getMaterialName());
        }

        dto.setDisplayPrice(displayPrice);
        dto.setInStock(activeVariants.stream().anyMatch(this::hasStock));
        dto.setTotalStock(activeVariants.stream()
                .map(ProductColor::getStockQuantity)
                .filter(Objects::nonNull)
                .mapToInt(qty -> Math.max(qty, 0))
                .sum());

        dto.setDefaultVariantId(defaultVariant != null ? defaultVariant.getId() : null);
        dto.setDisplayImage(resolveDisplayImage(defaultVariant, activeVariants));
        dto.setColors(buildColorDots(activeVariants));

        return dto;
    }

    private List<ProductSummaryCardDto.ColorDotDto> buildColorDots(List<ProductColor> variants) {
        LinkedHashMap<Integer, ProductSummaryCardDto.ColorDotDto> map = new LinkedHashMap<>();

        for (ProductColor pc : variants) {
            if (pc.getColorID() == null || pc.getColorID().getId() == null) {
                continue;
            }

            Integer colorId = pc.getColorID().getId();

            map.putIfAbsent(
                    colorId,
                    new ProductSummaryCardDto.ColorDotDto(
                            colorId,
                            pc.getColorID().getColorName(),
                            pc.getColorID().getColorCode()
                    )
            );
        }

        return new ArrayList<>(map.values());
    }

    private ProductColor pickDefaultVariant(List<ProductColor> variants) {
        List<ProductColor> activeVariants = variants.stream()
                .filter(Objects::nonNull)
                .filter(pc -> "ACTIVE".equalsIgnoreCase(safe(pc.getStatus())))
                .toList();

        ProductColor representative = activeVariants.stream()
                .filter(pc -> Boolean.TRUE.equals(pc.getIsRepresentative()))
                .findFirst()
                .orElse(null);

        if (representative != null) {
            return representative;
        }

        return activeVariants.stream()
                .sorted(
                        Comparator
                                .comparing((ProductColor pc) -> hasStock(pc) ? 0 : 1)
                                .thenComparing(pc -> hasMainImage(pc) ? 0 : 1)
                                .thenComparing(ProductColor::getId, Comparator.nullsLast(Integer::compareTo))
                )
                .findFirst()
                .orElse(null);
    }

    private String resolveDisplayImage(ProductColor defaultVariant, List<ProductColor> variants) {
        String image = findMainImage(defaultVariant);
        if (image != null) {
            return image;
        }

        image = findAnyImage(defaultVariant);
        if (image != null) {
            return image;
        }

        for (ProductColor pc : variants) {
            image = findMainImage(pc);
            if (image != null) {
                return image;
            }
        }

        for (ProductColor pc : variants) {
            image = findAnyImage(pc);
            if (image != null) {
                return image;
            }
        }

        return null;
    }

    private String findMainImage(ProductColor pc) {
        if (pc == null || pc.getImages() == null) {
            return null;
        }

        return pc.getImages().stream()
                .filter(Objects::nonNull)
                .filter(img -> Boolean.TRUE.equals(img.getIsMain()))
                .map(Image::getImageUrl)
                .filter(url -> url != null && !url.isBlank())
                .findFirst()
                .orElse(null);
    }

    private String findAnyImage(ProductColor pc) {
        if (pc == null || pc.getImages() == null) {
            return null;
        }

        return pc.getImages().stream()
                .filter(Objects::nonNull)
                .map(Image::getImageUrl)
                .filter(url -> url != null && !url.isBlank())
                .findFirst()
                .orElse(null);
    }

    private boolean hasMainImage(ProductColor pc) {
        return findMainImage(pc) != null;
    }

    private boolean hasStock(ProductColor pc) {
        return pc != null && pc.getStockQuantity() != null && pc.getStockQuantity() > 0;
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
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