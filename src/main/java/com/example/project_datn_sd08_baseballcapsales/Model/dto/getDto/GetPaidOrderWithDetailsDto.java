package com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto;

import com.example.project_datn_sd08_baseballcapsales.Model.entity.Order;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.OrderDetail;
import com.example.project_datn_sd08_baseballcapsales.Model.enums.OrderStatus;
import com.example.project_datn_sd08_baseballcapsales.Model.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetPaidOrderWithDetailsDto {

    private static final ZoneId VN_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");
    private static final DateTimeFormatter VN_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private Integer orderId;
    private String trackingCode;
    private String orderType;

    private Integer accountId;
    private String accountUsername;

    private Integer employeeId;
    private String employeeName;

    private String customerName;
    private String customerPhone;

    private String orderDate;
    private OrderStatus orderStatus;
    private String shippingAddress;
    private String note;
    private String couponCode;
    private BigDecimal totalAmount;
    private PaymentStatus paymentStatus;
    private String paymentMethod;

    private List<OrderItemDetailsDto> items;

    public GetPaidOrderWithDetailsDto(
            Order order,
            PaymentStatus paymentStatus,
            String paymentMethod,
            List<OrderDetail> orderDetails
    ) {
        this.orderId = order.getId();
        this.trackingCode = order.getTrackingCode();
        this.orderType = order.getOrderType();

        if (order.getAccountID() != null) {
            this.accountId = order.getAccountID().getId();
            this.accountUsername = order.getAccountID().getUsername();
        } else {
            this.accountId = null;
            this.accountUsername = null;
        }

        if (order.getEmployeeID() != null) {
            this.employeeId = order.getEmployeeID().getId();
            this.employeeName = order.getEmployeeID().getUsername();
        } else {
            this.employeeId = null;
            this.employeeName = null;
        }

        this.customerName = order.getCustomerName();
        this.customerPhone = order.getCustomerPhone();

        this.orderDate = order.getOrderDate() != null
                ? order.getOrderDate().atZone(VN_ZONE).format(VN_FORMAT)
                : null;

        this.orderStatus = order.getStatus();
        this.shippingAddress = order.getShippingAddress();
        this.note = order.getNote();
        this.couponCode = order.getCouponID() != null
                ? order.getCouponID().getCouponCode()
                : null;
        this.totalAmount = order.getTotalAmount();
        this.paymentStatus = paymentStatus;
        this.paymentMethod = paymentMethod;
        this.items = new ArrayList<>();

        if (orderDetails != null) {
            for (OrderDetail detail : orderDetails) {
                if (detail == null || detail.getProductColorID() == null) {
                    continue;
                }
                this.items.add(new OrderItemDetailsDto(detail));
            }
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemDetailsDto {
        private Integer orderDetailId;
        private Integer productId;
        private String productName;
        private Integer quantity;
        private BigDecimal price;
        private String colorName;
        private String sizeName;
        private String imageUrl;

        private Integer returnedQuantity;
        private Integer returnableQuantity;
        private Integer remainingQuantity;
        private BigDecimal returnedAmount;

        private Integer shippingReturnedQuantity;
        private Integer completedReturnedQuantity;

        public OrderItemDetailsDto(OrderDetail detail) {
            this.orderDetailId = detail.getId();

            if (detail.getProductColorID() != null) {
                if (detail.getProductColorID().getProductID() != null) {
                    this.productId = detail.getProductColorID().getProductID().getId();
                    this.productName = detail.getProductColorID().getProductID().getProductName();
                }

                this.colorName = detail.getProductColorID().getColorID() != null
                        ? detail.getProductColorID().getColorID().getColorName()
                        : "Unknown";

                this.sizeName = detail.getProductColorID().getSizeID() != null
                        ? detail.getProductColorID().getSizeID().getSizeName()
                        : null;

                if (detail.getProductColorID().getImages() != null
                        && !detail.getProductColorID().getImages().isEmpty()) {
                    this.imageUrl = detail.getProductColorID().getImages().get(0).getImageUrl();
                }
            }

            int boughtQuantity = detail.getQuantity() == null ? 0 : detail.getQuantity();
            int returnedQty = detail.getReturnedQuantity() == null ? 0 : detail.getReturnedQuantity();
            int shippingReturnedQty = detail.getShippingReturnedQuantity() == null ? 0 : detail.getShippingReturnedQuantity();
            int completedReturnedQty = detail.getCompletedReturnedQuantity() == null ? 0 : detail.getCompletedReturnedQuantity();
            int remainingQty = Math.max(0, boughtQuantity - returnedQty);

            this.quantity = boughtQuantity;
            this.returnedQuantity = returnedQty;
            this.shippingReturnedQuantity = shippingReturnedQty;
            this.completedReturnedQuantity = completedReturnedQty;
            this.returnableQuantity = remainingQty;
            this.remainingQuantity = remainingQty;
            this.price = detail.getPrice();

            BigDecimal safePrice = detail.getPrice() == null ? BigDecimal.ZERO : detail.getPrice();
            this.returnedAmount = safePrice.multiply(BigDecimal.valueOf(returnedQty));
        }
    }
}