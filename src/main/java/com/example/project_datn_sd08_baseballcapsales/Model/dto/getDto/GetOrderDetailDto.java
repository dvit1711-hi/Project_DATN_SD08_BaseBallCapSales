package com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto;

import com.example.project_datn_sd08_baseballcapsales.Model.entity.OrderDetail;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetOrderDetailDto {

    private Integer id;

//    @NotNull
//    @ManyToOne(fetch = FetchType.LAZY, optional = false)
//    private Order orderID;
//
//    @NotNull
//    @ManyToOne(fetch = FetchType.LAZY, optional = false)
//    private Product productID;

    @NotNull
    private Integer quantity;

    @NotNull
    private BigDecimal price;

    @Size(max = 50)
    @NotNull
    @Nationalized
    private String accountCode;

    @Size(max = 200)
    @Nationalized
    private String productName;

    public GetOrderDetailDto(OrderDetail od) {
        this.id = od.getId();
        this.quantity = od.getQuantity();
        this.price = od.getPrice();
        if (od.getProductID() != null) {
            this.productName = od.getProductID().getProductName();
        }

        if (od.getOrderID() != null) {
            this.accountCode = od.getOrderID().getAccountID().getEmail();
        }
    }
}
