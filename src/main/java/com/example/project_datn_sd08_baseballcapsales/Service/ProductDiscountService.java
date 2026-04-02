package com.example.project_datn_sd08_baseballcapsales.Service;

import com.example.project_datn_sd08_baseballcapsales.Model.dto.PostDto.PostProductDiscountDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.PutDto.PutProductDiscountDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto.GetProductDiscountDto;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.ProductColor;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.ProductDiscount;
import com.example.project_datn_sd08_baseballcapsales.Repository.ProductDiscountRepository;
import com.example.project_datn_sd08_baseballcapsales.Repository.ProductColorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class ProductDiscountService {

    @Autowired
    private ProductDiscountRepository productDiscountRepository;

    @Autowired
    private ProductColorRepository productColorRepository;

    public List<GetProductDiscountDto> getAllDiscounts() {
        return productDiscountRepository.findAll()
                .stream()
                .map(GetProductDiscountDto::new)
                .toList();
    }

    public GetProductDiscountDto getDiscountById(Integer id) {
        return productDiscountRepository.findById(id)
                .map(GetProductDiscountDto::new)
                .orElseThrow(() -> new RuntimeException("Giảm giá sản phẩm không tìm thấy"));
    }

    public List<GetProductDiscountDto> getDiscountsByProductColorId(Integer productColorId) {
        return productDiscountRepository.findByProductColorId(productColorId)
                .stream()
                .map(GetProductDiscountDto::new)
                .toList();
    }

    public List<GetProductDiscountDto> getActiveDiscounts() {
        return productDiscountRepository.findAllByActive(true)
                .stream()
                .map(GetProductDiscountDto::new)
                .toList();
    }

    public List<GetProductDiscountDto> getDiscountsByReason(String reason) {
        return productDiscountRepository.findAllByReason(reason)
                .stream()
                .map(GetProductDiscountDto::new)
                .toList();
    }

    public ProductDiscount getActiveDiscountForProductColor(Integer productColorId) {
        if (productColorId == null) {
            return null;
        }

        return productDiscountRepository.findByProductColorIdAndActive(productColorId, true)
                .filter(this::isDiscountCurrentlyValid)
                .orElse(null);
    }

    public BigDecimal getDiscountedPrice(ProductColor productColor) {
        if (productColor == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal basePrice = productColor.getPrice() == null ? BigDecimal.ZERO : productColor.getPrice();
        ProductDiscount discount = getActiveDiscountForProductColor(productColor.getId());
        if (discount == null) {
            return basePrice;
        }

        return calculateDiscountedPrice(basePrice, discount);
    }

    public ProductDiscount createDiscount(PostProductDiscountDto dto) {
        ProductColor productColor = productColorRepository.findById(dto.getProductColorId())
                .orElseThrow(() -> new RuntimeException("Biến thể màu sản phẩm không tìm thấy"));

        ProductDiscount discount = new ProductDiscount();
        discount.setProductColor(productColor);
        discount.setDiscountType(dto.getDiscountType());
        discount.setDiscountValue(dto.getDiscountValue());
        discount.setMaxDiscountValue(dto.getMaxDiscountValue());
        discount.setQuantity(dto.getQuantity());
        discount.setQuantityUsed(0);
        discount.setStartDate(dto.getStartDate());
        discount.setEndDate(dto.getEndDate());
        discount.setActive(dto.getActive() != null ? dto.getActive() : true);
        discount.setDescription(dto.getDescription());
        discount.setReason(dto.getReason());

        return productDiscountRepository.save(discount);
    }

    public ProductDiscount updateDiscount(Integer id, PutProductDiscountDto dto) {
        ProductDiscount discount = productDiscountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Giảm giá sản phẩm không tìm thấy"));

        if (dto.getDiscountType() != null)
            discount.setDiscountType(dto.getDiscountType());
        if (dto.getDiscountValue() != null)
            discount.setDiscountValue(dto.getDiscountValue());
        if (dto.getMaxDiscountValue() != null)
            discount.setMaxDiscountValue(dto.getMaxDiscountValue());
        if (dto.getQuantity() != null)
            discount.setQuantity(dto.getQuantity());
        if (dto.getStartDate() != null)
            discount.setStartDate(dto.getStartDate());
        if (dto.getEndDate() != null)
            discount.setEndDate(dto.getEndDate());
        if (dto.getActive() != null)
            discount.setActive(dto.getActive());
        if (dto.getDescription() != null)
            discount.setDescription(dto.getDescription());
        if (dto.getReason() != null)
            discount.setReason(dto.getReason());

        return productDiscountRepository.save(discount);
    }

    public void deleteDiscount(Integer id) {
        if (!productDiscountRepository.existsById(id)) {
            throw new RuntimeException("Giảm giá sản phẩm không tìm thấy");
        }
        productDiscountRepository.deleteById(id);
    }

    public boolean isDiscountValid(Integer discountId) {
        ProductDiscount discount = productDiscountRepository.findById(discountId)
                .orElseThrow(() -> new RuntimeException("Giảm giá sản phẩm không tìm thấy"));

        LocalDate today = LocalDate.now();
        boolean isWithinDateRange = !today.isBefore(discount.getStartDate()) && !today.isAfter(discount.getEndDate());
        boolean hasQuantity = discount.getQuantity() > discount.getQuantityUsed();
        boolean isActive = discount.getActive();

        return isWithinDateRange && hasQuantity && isActive;
    }

    private boolean isDiscountCurrentlyValid(ProductDiscount discount) {
        if (discount == null) {
            return false;
        }

        LocalDate today = LocalDate.now();
        boolean isWithinDateRange = (discount.getStartDate() == null || !today.isBefore(discount.getStartDate()))
                && (discount.getEndDate() == null || !today.isAfter(discount.getEndDate()));
        boolean hasQuantity = discount.getQuantity() == null || discount.getQuantity() > discount.getQuantityUsed();
        boolean isActive = Boolean.TRUE.equals(discount.getActive());

        return isWithinDateRange && hasQuantity && isActive;
    }

    private BigDecimal calculateDiscountedPrice(BigDecimal basePrice, ProductDiscount discount) {
        if (discount == null || basePrice == null) {
            return basePrice == null ? BigDecimal.ZERO : basePrice;
        }

        BigDecimal discountValue = discount.getDiscountValue() == null ? BigDecimal.ZERO : discount.getDiscountValue();
        if (discountValue.compareTo(BigDecimal.ZERO) <= 0) {
            return basePrice;
        }

        String type = discount.getDiscountType() == null ? "" : discount.getDiscountType().trim().toLowerCase();
        BigDecimal discountAmount;

        if ("percent".equals(type)) {
            discountAmount = basePrice
                    .multiply(discountValue)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            if (discount.getMaxDiscountValue() != null && discount.getMaxDiscountValue().compareTo(BigDecimal.ZERO) > 0) {
                discountAmount = discountAmount.min(discount.getMaxDiscountValue());
            }
        } else if ("fixed".equals(type)) {
            discountAmount = discountValue;
        } else {
            return basePrice;
        }

        return basePrice.subtract(discountAmount).max(BigDecimal.ZERO);
    }

    public void incrementQuantityUsed(Integer discountId) {
        ProductDiscount discount = productDiscountRepository.findById(discountId)
                .orElseThrow(() -> new RuntimeException("Giảm giá sản phẩm không tìm thấy"));

        discount.setQuantityUsed(discount.getQuantityUsed() + 1);
        productDiscountRepository.save(discount);
    }
}
