package com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto;

import com.example.project_datn_sd08_baseballcapsales.Model.entity.ProductDiscount;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetProductDiscountDto {
    private Integer id;

    private Integer productColorId;

    private Integer productId;

    private String productName;

    private String colorName;

    private String colorCode;

    private String productImage;

    @Size(max = 20)
    private String discountType;

    private BigDecimal discountValue;

    private BigDecimal maxDiscountValue;

    private Integer quantity;

    private Integer quantityUsed;

    private LocalDate startDate;

    private LocalDate endDate;

    private Boolean active;

    @Size(max = 500)
    private String description;

    @Size(max = 50)
    private String reason;

    private LocalDate createdAt;

    public GetProductDiscountDto(ProductDiscount productDiscount) {
        this.id = productDiscount.getId();
        this.productColorId = productDiscount.getProductColor().getId();
        this.productId = productDiscount.getProductColor().getProductID().getId();
        this.productName = productDiscount.getProductColor().getProductID().getProductName();
        this.colorName = productDiscount.getProductColor().getColorID().getColorName();
        this.colorCode = productDiscount.getProductColor().getColorID().getColorCode();
        this.productImage = null; // Could be retrieved from ProductImages table if needed
        this.discountType = productDiscount.getDiscountType();
        this.discountValue = productDiscount.getDiscountValue();
        this.maxDiscountValue = productDiscount.getMaxDiscountValue();
        this.quantity = productDiscount.getQuantity();
        this.quantityUsed = productDiscount.getQuantityUsed();
        this.startDate = productDiscount.getStartDate();
        this.endDate = productDiscount.getEndDate();
        this.active = productDiscount.getActive();
        this.description = productDiscount.getDescription();
        this.reason = productDiscount.getReason();
        this.createdAt = productDiscount.getCreatedAt();
    }
}
