package com.example.project_datn_sd08_baseballcapsales.Service.Impl;

import com.example.project_datn_sd08_baseballcapsales.Model.dto.PostDto.*;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.PutDto.PutOfflineOrderItemDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto.*;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.*;
import com.example.project_datn_sd08_baseballcapsales.Repository.*;
import com.example.project_datn_sd08_baseballcapsales.Service.PosService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PosServiceImpl implements PosService {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductColorRepository productColorRepository;
    private final DiscountCouponRepository discountCouponRepository;
    private final PaymentRepository paymentRepository;
    private final AccountRepository accountRepository;

    @Override
    public List<PosProductColorGetDto> searchProducts(String keyword) {
        return productColorRepository.searchForPos(keyword)
                .stream()
                .map(this::mapProductColorDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PosCustomerGetDto> searchCustomers(String keyword) {
        return accountRepository.searchCustomers(keyword == null ? "" : keyword)
                .stream()
                .map(this::mapCustomerDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PosOrderGetDto createOfflineOrder(PostOfflineOrderDto dto, String email) {
        Account employee = accountRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên đăng nhập"));

        Account customer = null;
        if (dto.getAccountId() != null) {
            customer = accountRepository.findById(dto.getAccountId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));
        }

        if (customer == null && !StringUtils.hasText(dto.getCustomerName())) {
            throw new RuntimeException("Khách lẻ phải nhập tên khách hàng");
        }

        Order order = new Order();
        order.setOrderType("OFFLINE");
        order.setStatus("Pending");
        order.setEmployeeID(employee);
        order.setAccountID(customer);

        if (customer != null) {
            order.setCustomerName(
                    StringUtils.hasText(dto.getCustomerName()) ? dto.getCustomerName() : customer.getUsername()
            );
            order.setCustomerPhone(
                    StringUtils.hasText(dto.getCustomerPhone()) ? dto.getCustomerPhone() : customer.getPhoneNumber()
            );
        } else {
            order.setCustomerName(dto.getCustomerName());
            order.setCustomerPhone(dto.getCustomerPhone());
        }

        order.setNote(dto.getNote());
        order.setShippingAddress(dto.getShippingAddress());
        order.setTotalAmount(BigDecimal.ZERO);

        order = orderRepository.save(order);
        return getOrder(order.getId());
    }

    @Override
    public PosOrderGetDto getOrder(Integer orderId) {
        Order order = getOrderEntity(orderId);
        return mapOrderDto(order);
    }

    @Override
    @Transactional
    public PosOrderGetDto addItem(Integer orderId, PostOfflineOrderItemDto dto) {
        if (dto.getQuantity() == null || dto.getQuantity() <= 0) {
            throw new RuntimeException("Số lượng phải lớn hơn 0");
        }

        Order order = getPendingOrder(orderId);

        ProductColor productColor = productColorRepository.findById(dto.getProductColorId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        if (productColor.getStockQuantity() == null || productColor.getStockQuantity() <= 0) {
            throw new RuntimeException("Sản phẩm đã hết hàng");
        }

        if (productColor.getPrice() == null || productColor.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Sản phẩm chưa có giá bán");
        }

        OrderDetail detail = orderDetailRepository
                .findByOrderID_IdAndProductColorID_Id(orderId, dto.getProductColorId())
                .orElse(null);

        if (detail == null) {
            detail = new OrderDetail();
            detail.setOrderID(order);
            detail.setProductColorID(productColor);
            detail.setQuantity(dto.getQuantity());
            detail.setPrice(productColor.getPrice());
        } else {
            int newQuantity = detail.getQuantity() + dto.getQuantity();
            if (newQuantity > productColor.getStockQuantity()) {
                throw new RuntimeException("Số lượng vượt quá tồn kho");
            }
            detail.setQuantity(newQuantity);
        }

        if (detail.getQuantity() > productColor.getStockQuantity()) {
            throw new RuntimeException("Số lượng vượt quá tồn kho");
        }

        orderDetailRepository.save(detail);
        recalculateOrder(order);
        orderRepository.save(order);

        return getOrder(orderId);
    }

    @Override
    @Transactional
    public PosOrderGetDto updateItem(Integer orderId, Integer orderDetailId, PutOfflineOrderItemDto dto) {
        if (dto.getQuantity() == null || dto.getQuantity() <= 0) {
            throw new RuntimeException("Số lượng phải lớn hơn 0");
        }

        Order order = getPendingOrder(orderId);

        OrderDetail detail = orderDetailRepository.findById(orderDetailId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy dòng sản phẩm"));

        if (!detail.getOrderID().getId().equals(order.getId())) {
            throw new RuntimeException("Dòng sản phẩm không thuộc đơn này");
        }

        ProductColor productColor = detail.getProductColorID();
        if (productColor.getStockQuantity() == null || dto.getQuantity() > productColor.getStockQuantity()) {
            throw new RuntimeException("Số lượng vượt quá tồn kho");
        }

        detail.setQuantity(dto.getQuantity());
        orderDetailRepository.save(detail);

        recalculateOrder(order);
        orderRepository.save(order);

        return getOrder(orderId);
    }

    @Override
    @Transactional
    public PosOrderGetDto removeItem(Integer orderId, Integer orderDetailId) {
        Order order = getPendingOrder(orderId);

        OrderDetail detail = orderDetailRepository.findById(orderDetailId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy dòng sản phẩm"));

        if (!detail.getOrderID().getId().equals(order.getId())) {
            throw new RuntimeException("Dòng sản phẩm không thuộc đơn này");
        }

        orderDetailRepository.delete(detail);

        recalculateOrder(order);
        orderRepository.save(order);

        return getOrder(orderId);
    }

    @Override
    @Transactional
    public PosOrderGetDto applyCoupon(Integer orderId, PostApplyCouponDto dto) {
        Order order = getPendingOrder(orderId);

        if (!StringUtils.hasText(dto.getCouponCode())) {
            order.setCouponID(null);
            recalculateOrder(order);
            orderRepository.save(order);
            return getOrder(orderId);
        }

        DiscountCoupon coupon = discountCouponRepository.findByCouponCodeIgnoreCase(dto.getCouponCode().trim())
                .orElseThrow(() -> new RuntimeException("Mã giảm giá không tồn tại"));

        BigDecimal subtotal = calculateSubtotal(order);
        validateCoupon(coupon, subtotal);

        order.setCouponID(coupon);
        recalculateOrder(order);
        orderRepository.save(order);

        return getOrder(orderId);
    }

    @Override
    @Transactional
    public PosOrderGetDto checkout(Integer orderId, PostCheckoutOrderDto dto) {
        Order order = getPendingOrder(orderId);

        List<OrderDetail> details = orderDetailRepository.findByOrderID_Id(orderId);
        if (details.isEmpty()) {
            throw new RuntimeException("Đơn hàng chưa có sản phẩm");
        }

        for (OrderDetail detail : details) {
            ProductColor productColor = productColorRepository.findById(detail.getProductColorID().getId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

            if (productColor.getStockQuantity() == null || productColor.getStockQuantity() < detail.getQuantity()) {
                throw new RuntimeException("Tồn kho không đủ cho sản phẩm: " + productColor.getProductID().getProductName());
            }
        }

        if (order.getCouponID() != null) {
            validateCoupon(order.getCouponID(), calculateSubtotal(order));
        }

        recalculateOrder(order);

        if ("CASH".equalsIgnoreCase(dto.getMethod())) {
            if (dto.getCashReceived() == null || dto.getCashReceived().compareTo(order.getTotalAmount()) < 0) {
                throw new RuntimeException("Tiền khách đưa không đủ");
            }
        }

        for (OrderDetail detail : details) {
            ProductColor productColor = detail.getProductColorID();
            productColor.setStockQuantity(productColor.getStockQuantity() - detail.getQuantity());
            productColorRepository.save(productColor);
        }

        if (order.getCouponID() != null && order.getCouponID().getQuantity() != null) {
            if (order.getCouponID().getQuantity() <= 0) {
                throw new RuntimeException("Mã giảm giá đã hết lượt sử dụng");
            }
            order.getCouponID().setQuantity(order.getCouponID().getQuantity() - 1);
            discountCouponRepository.save(order.getCouponID());
        }

        order.setStatus("Completed");
        orderRepository.save(order);

        Payment payment = new Payment();
        payment.setOrderID(order);
        payment.setAmount(order.getTotalAmount());
        payment.setMethod(dto.getMethod());
        payment.setStatus("SUCCESS");
        paymentRepository.save(payment);

        return getOrder(orderId);
    }

    private Order getOrderEntity(Integer orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
    }

    private Order getPendingOrder(Integer orderId) {
        Order order = getOrderEntity(orderId);
        if (!"Pending".equalsIgnoreCase(order.getStatus())) {
            throw new RuntimeException("Chỉ thao tác được với đơn Pending");
        }
        return order;
    }

    private BigDecimal calculateSubtotal(Order order) {
        List<OrderDetail> details = orderDetailRepository.findByOrderID_Id(order.getId());
        return details.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateDiscount(Order order, BigDecimal subtotal) {
        DiscountCoupon coupon = order.getCouponID();
        if (coupon == null) {
            return BigDecimal.ZERO;
        }

        if (!isCouponValid(coupon, subtotal)) {
            return BigDecimal.ZERO;
        }

        BigDecimal discount = BigDecimal.ZERO;
        if ("percent".equalsIgnoreCase(coupon.getDiscountType())) {
            discount = subtotal.multiply(coupon.getDiscountValue()).divide(BigDecimal.valueOf(100));
        } else if ("fixed".equalsIgnoreCase(coupon.getDiscountType())) {
            discount = coupon.getDiscountValue();
        }

        if (coupon.getMaxDiscountValue() != null
                && coupon.getMaxDiscountValue().compareTo(BigDecimal.ZERO) > 0
                && discount.compareTo(coupon.getMaxDiscountValue()) > 0) {
            discount = coupon.getMaxDiscountValue();
        }

        if (discount.compareTo(subtotal) > 0) {
            discount = subtotal;
        }

        return discount;
    }

    private void recalculateOrder(Order order) {
        BigDecimal subtotal = calculateSubtotal(order);
        BigDecimal discount = calculateDiscount(order, subtotal);
        BigDecimal total = subtotal.subtract(discount);

        if (total.compareTo(BigDecimal.ZERO) < 0) {
            total = BigDecimal.ZERO;
        }

        order.setTotalAmount(total);
    }

    private boolean isCouponValid(DiscountCoupon coupon, BigDecimal subtotal) {
        LocalDate today = LocalDate.now();

        if (coupon.getActive() != null && !coupon.getActive()) {
            return false;
        }

        if (coupon.getStartDate() != null && today.isBefore(coupon.getStartDate())) {
            return false;
        }

        if (coupon.getEndDate() != null && today.isAfter(coupon.getEndDate())) {
            return false;
        }

        if (coupon.getQuantity() != null && coupon.getQuantity() <= 0) {
            return false;
        }

        return coupon.getMinOrderValue() == null || subtotal.compareTo(coupon.getMinOrderValue()) >= 0;
    }

    private void validateCoupon(DiscountCoupon coupon, BigDecimal subtotal) {
        if (!isCouponValid(coupon, subtotal)) {
            throw new RuntimeException("Mã giảm giá không hợp lệ hoặc không đủ điều kiện áp dụng");
        }
    }

    private PosProductColorGetDto mapProductColorDto(ProductColor pc) {
        PosProductColorGetDto dto = new PosProductColorGetDto();
        dto.setProductColorId(pc.getId());
        dto.setProductName(pc.getProductID().getProductName());
        dto.setColorName(pc.getColorID() != null ? pc.getColorID().getColorName() : null);
        dto.setSizeName(pc.getSizeID() != null ? pc.getSizeID().getSizeName() : null);
        dto.setPrice(pc.getPrice());
        dto.setStockQuantity(pc.getStockQuantity());

        dto.setDisplayName(
                pc.getProductID().getProductName()
                        + " - " + (pc.getColorID() != null ? pc.getColorID().getColorName() : "-")
                        + " - " + (pc.getSizeID() != null ? pc.getSizeID().getSizeName() : "-")
                        + " - " + formatMoney(pc.getPrice())
                        + " - Tồn: " + pc.getStockQuantity()
        );

        if (pc.getImages() != null && !pc.getImages().isEmpty()) {
            Image firstImage = pc.getImages().get(0);

            // đổi getImageUrl() thành tên field thật trong entity Image của bạn
            dto.setImageUrl(firstImage.getImageUrl());
        } else {
            dto.setImageUrl(null);
        }

        return dto;
    }

    private PosCustomerGetDto mapCustomerDto(Account account) {
        PosCustomerGetDto dto = new PosCustomerGetDto();
        dto.setAccountId(account.getId());
        dto.setUsername(account.getUsername());
        dto.setEmail(account.getEmail());
        dto.setPhoneNumber(account.getPhoneNumber());
        dto.setDisplayName(account.getUsername() + " - " + account.getPhoneNumber());
        return dto;
    }

    private PosOrderGetDto mapOrderDto(Order order) {
        List<OrderDetail> details = orderDetailRepository.findByOrderID_Id(order.getId());
        BigDecimal subtotal = details.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal discount = calculateDiscount(order, subtotal);

        PosOrderGetDto dto = new PosOrderGetDto();
        dto.setOrderId(order.getId());
        dto.setOrderDate(order.getOrderDate());
        dto.setOrderType(order.getOrderType());
        dto.setStatus(order.getStatus());

        dto.setCustomerId(order.getAccountID() != null ? order.getAccountID().getId() : null);
        dto.setCustomerName(order.getCustomerName());
        dto.setCustomerPhone(order.getCustomerPhone());

        dto.setEmployeeId(order.getEmployeeID() != null ? order.getEmployeeID().getId() : null);
        dto.setEmployeeName(order.getEmployeeID() != null ? order.getEmployeeID().getUsername() : null);

        dto.setNote(order.getNote());
        dto.setShippingAddress(order.getShippingAddress());
        dto.setCouponCode(order.getCouponID() != null ? order.getCouponID().getCouponCode() : null);

        dto.setSubtotal(subtotal);
        dto.setDiscountAmount(discount);
        dto.setTotalAmount(order.getTotalAmount());

        dto.setItems(details.stream().map(this::mapOrderItemDto).collect(Collectors.toList()));
        return dto;
    }

    private PosOrderItemGetDto mapOrderItemDto(OrderDetail item) {
        PosOrderItemGetDto dto = new PosOrderItemGetDto();
        dto.setOrderDetailId(item.getId());
        dto.setProductColorId(item.getProductColorID().getId());
        dto.setProductName(item.getProductColorID().getProductID().getProductName());
        dto.setColorName(item.getProductColorID().getColorID().getColorName());
        dto.setSizeName(item.getProductColorID().getSizeID().getSizeName());
        dto.setQuantity(item.getQuantity());
        dto.setPrice(item.getPrice());
        dto.setLineTotal(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        dto.setStockQuantity(item.getProductColorID().getStockQuantity());
        return dto;
    }

    private String formatMoney(BigDecimal amount) {
        if (amount == null) return "0";
        return String.format("%,.0f", amount);
    }
}