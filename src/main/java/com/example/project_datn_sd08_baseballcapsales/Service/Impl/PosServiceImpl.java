package com.example.project_datn_sd08_baseballcapsales.Service.Impl;

import com.example.project_datn_sd08_baseballcapsales.Model.dto.PostDto.*;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.PutDto.PutOfflineOrderInfoDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.PutDto.PutOfflineOrderItemDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto.*;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.*;
import com.example.project_datn_sd08_baseballcapsales.Model.enums.OrderStatus;
import com.example.project_datn_sd08_baseballcapsales.Model.enums.PaymentStatus;
import com.example.project_datn_sd08_baseballcapsales.Repository.*;
import com.example.project_datn_sd08_baseballcapsales.Service.PosService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Comparator;
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
    private final ProductDiscountRepository productDiscountRepository;

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
    public List<PosOrderGetDto> getPendingOrders(String email) {
        Account employee = getCurrentEmployee(email);

        return orderRepository
                .findByEmployeeID_IdAndOrderTypeIgnoreCaseAndStatusOrderByOrderDateDesc(
                        employee.getId(),
                        "OFFLINE",
                        OrderStatus.PENDING_PAYMENT
                )
                .stream()
                .map(this::mapOrderDto)
                .toList();
    }

    @Override
    @Transactional
    public PosOrderGetDto createOfflineOrder(PostOfflineOrderDto dto, String email) {
        Account employee = getCurrentEmployee(email);

        long pendingCount = orderRepository
                .countByEmployeeID_IdAndOrderTypeIgnoreCaseAndStatus(
                        employee.getId(),
                        "OFFLINE",
                        OrderStatus.PENDING_PAYMENT
                );

        if (pendingCount >= 10) {
            throw new RuntimeException("Mỗi nhân viên chỉ được giữ tối đa 10 đơn hàng chờ");
        }

        Account customer = null;
        if (dto.getAccountId() != null) {
            customer = accountRepository.findById(dto.getAccountId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));
        }

        Order order = new Order();
        order.setOrderType("OFFLINE");
        order.setStatus(OrderStatus.PENDING_PAYMENT);
        order.setEmployeeID(employee);
        order.setAccountID(customer);

        if (customer != null) {
            order.setCustomerName(
                    StringUtils.hasText(dto.getCustomerName())
                            ? dto.getCustomerName().trim()
                            : customer.getUsername()
            );
            order.setCustomerPhone(
                    StringUtils.hasText(dto.getCustomerPhone())
                            ? dto.getCustomerPhone().trim()
                            : customer.getPhoneNumber()
            );
        } else {
            order.setCustomerName(normalizeText(dto.getCustomerName()));
            order.setCustomerPhone(normalizeText(dto.getCustomerPhone()));
        }

        order.setNote(normalizeText(dto.getNote()));
        order.setShippingAddress(normalizeText(dto.getShippingAddress()));
        order.setTotalAmount(BigDecimal.ZERO);

        order = orderRepository.save(order);
        return getOrder(order.getId(), email);
    }

    @Override
    public PosOrderGetDto getOrder(Integer orderId, String email) {
        Order order = getOwnedOfflineOrder(orderId, email);
        return mapOrderDto(order);
    }

    @Override
    @Transactional
    public PosOrderGetDto updateOrderInfo(Integer orderId, PutOfflineOrderInfoDto dto, String email) {
        Order order = getPendingOrder(orderId, email);

        Account customer = null;
        if (dto.getAccountId() != null) {
            customer = accountRepository.findById(dto.getAccountId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));
        }

        order.setAccountID(customer);

        if (customer != null) {
            order.setCustomerName(
                    StringUtils.hasText(dto.getCustomerName())
                            ? dto.getCustomerName().trim()
                            : customer.getUsername()
            );
            order.setCustomerPhone(
                    StringUtils.hasText(dto.getCustomerPhone())
                            ? dto.getCustomerPhone().trim()
                            : customer.getPhoneNumber()
            );
        } else {
            order.setCustomerName(normalizeText(dto.getCustomerName()));
            order.setCustomerPhone(normalizeText(dto.getCustomerPhone()));
        }

        order.setNote(normalizeText(dto.getNote()));
        order.setShippingAddress(normalizeText(dto.getShippingAddress()));

        orderRepository.save(order);
        return getOrder(orderId, email);
    }

    @Override
    @Transactional
    public PosOrderGetDto addItem(Integer orderId, PostOfflineOrderItemDto dto, String email) {
        if (dto.getQuantity() == null || dto.getQuantity() <= 0) {
            throw new RuntimeException("Số lượng phải lớn hơn 0");
        }

        Order order = getPendingOrder(orderId, email);

        ProductColor productColor = productColorRepository.findById(dto.getProductColorId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        if (productColor.getStockQuantity() == null || productColor.getStockQuantity() <= 0) {
            throw new RuntimeException("Sản phẩm đã hết hàng");
        }

        ProductPriceData priceData = resolveProductPrice(productColor);

        if (priceData.getFinalPrice() == null || priceData.getFinalPrice().compareTo(BigDecimal.ZERO) <= 0) {
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
            detail.setPrice(priceData.getFinalPrice());
        } else {
            int newQuantity = detail.getQuantity() + dto.getQuantity();
            if (newQuantity > productColor.getStockQuantity()) {
                throw new RuntimeException("Số lượng vượt quá tồn kho");
            }
            detail.setQuantity(newQuantity);
            detail.setPrice(priceData.getFinalPrice());
        }

        if (detail.getQuantity() > productColor.getStockQuantity()) {
            throw new RuntimeException("Số lượng vượt quá tồn kho");
        }

        orderDetailRepository.save(detail);
        recalculateOrder(order);
        orderRepository.save(order);

        return getOrder(orderId, email);
    }

    @Override
    @Transactional
    public PosOrderGetDto updateItem(Integer orderId, Integer orderDetailId, PutOfflineOrderItemDto dto, String email) {
        if (dto.getQuantity() == null || dto.getQuantity() <= 0) {
            throw new RuntimeException("Số lượng phải lớn hơn 0");
        }

        Order order = getPendingOrder(orderId, email);

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

        return getOrder(orderId, email);
    }

    @Override
    @Transactional
    public PosOrderGetDto removeItem(Integer orderId, Integer orderDetailId, String email) {
        Order order = getPendingOrder(orderId, email);

        OrderDetail detail = orderDetailRepository.findById(orderDetailId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy dòng sản phẩm"));

        if (!detail.getOrderID().getId().equals(order.getId())) {
            throw new RuntimeException("Dòng sản phẩm không thuộc đơn này");
        }

        orderDetailRepository.delete(detail);

        recalculateOrder(order);
        orderRepository.save(order);

        return getOrder(orderId, email);
    }

    @Override
    @Transactional
    public PosOrderGetDto applyCoupon(Integer orderId, PostApplyCouponDto dto, String email) {
        Order order = getPendingOrder(orderId, email);

        if (!StringUtils.hasText(dto.getCouponCode())) {
            order.setCouponID(null);
            recalculateOrder(order);
            orderRepository.save(order);
            return getOrder(orderId, email);
        }

        DiscountCoupon coupon = discountCouponRepository.findByCouponCodeIgnoreCase(dto.getCouponCode().trim())
                .orElseThrow(() -> new RuntimeException("Mã giảm giá không tồn tại"));

        BigDecimal subtotal = calculateSubtotal(order);
        validateCoupon(coupon, subtotal);

        order.setCouponID(coupon);
        recalculateOrder(order);
        orderRepository.save(order);

        return getOrder(orderId, email);
    }

    @Override
    public List<PosPromotionGetDto> getAvailablePromotions(Integer orderId, String email) {
        Order order = getOwnedOfflineOrder(orderId, email);
        BigDecimal subtotal = calculateSubtotal(order);
        LocalDate today = LocalDate.now();

        return discountCouponRepository.findAvailableForPos(today)
                .stream()
                .map(coupon -> mapPromotionDto(coupon, order, subtotal))
                .sorted(
                        Comparator.comparing(PosPromotionGetDto::isEligible).reversed()
                                .thenComparing(
                                        PosPromotionGetDto::getEstimatedDiscount,
                                        Comparator.nullsLast(Comparator.reverseOrder())
                                )
                                .thenComparing(
                                        dto -> dto.getMinOrderValue() == null ? BigDecimal.ZERO : dto.getMinOrderValue()
                                )
                )
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PosOrderGetDto checkout(Integer orderId, PostCheckoutOrderDto dto, String email) {
        Order order = getPendingOrder(orderId, email);

        List<OrderDetail> details = orderDetailRepository.findByOrderID_Id(orderId);
        if (details.isEmpty()) {
            throw new RuntimeException("Đơn hàng chưa có sản phẩm");
        }

        // Khách có tài khoản thì tự fill nếu thiếu
        if (order.getAccountID() != null) {
            if (!StringUtils.hasText(order.getCustomerName())) {
                order.setCustomerName(order.getAccountID().getUsername());
            }
            if (!StringUtils.hasText(order.getCustomerPhone())) {
                order.setCustomerPhone(order.getAccountID().getPhoneNumber());
            }
        } else {
            // Khách lẻ: cho phép để trống hoàn toàn
            order.setCustomerName(normalizeText(order.getCustomerName()));
            order.setCustomerPhone(normalizeText(order.getCustomerPhone()));
        }

        String method = dto.getMethod() == null ? "CASH" : dto.getMethod().trim().toUpperCase();

        if (!List.of("CASH", "BANKING").contains(method)) {
            throw new RuntimeException("Phương thức thanh toán không hợp lệ");
        }

        for (OrderDetail detail : details) {
            ProductColor productColor = productColorRepository.findById(detail.getProductColorID().getId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

            if (productColor.getStockQuantity() == null || productColor.getStockQuantity() < detail.getQuantity()) {
                throw new RuntimeException("Tồn kho không đủ cho sản phẩm: " + productColor.getProductID().getProductName());
            }
        }

        recalculateOrder(order);

        if ("CASH".equals(method)) {
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

        Payment payment = new Payment();
        payment.setOrderID(order);
        payment.setAmount(order.getTotalAmount());
        payment.setMethod(method);

        if ("CASH".equals(method)) {
            order.setStatus(OrderStatus.PAID);
            payment.setStatus(PaymentStatus.PAID);
        } else {
            order.setStatus(OrderStatus.PENDING_PAYMENT);
            payment.setStatus(PaymentStatus.UNPAID);
        }

        orderRepository.save(order);
        paymentRepository.save(payment);

        return getOrder(orderId, email);
    }

    @Override
    @Transactional
    public void cancelPendingOrder(Integer orderId, String email) {
        Account employee = getCurrentEmployee(email);

        Order order = orderRepository.findByIdAndEmployeeID_Id(orderId, employee.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng chờ"));

        if (!"OFFLINE".equalsIgnoreCase(order.getOrderType())
                || order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new RuntimeException("Chỉ cancel đơn pending");
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    private Account getCurrentEmployee(String email) {
        return accountRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên đăng nhập"));
    }

    private Order getOwnedOfflineOrder(Integer orderId, String email) {
        Account employee = getCurrentEmployee(email);

        Order order = orderRepository.findByIdAndEmployeeID_Id(orderId, employee.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng của nhân viên hiện tại"));

        if (!"OFFLINE".equalsIgnoreCase(order.getOrderType())) {
            throw new RuntimeException("Chỉ thao tác được với đơn OFFLINE");
        }

        return order;
    }

    private Order getPendingOrder(Integer orderId, String email) {
        Order order = getOwnedOfflineOrder(orderId, email);

        if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new RuntimeException("Chỉ thao tác được với đơn PENDING_PAYMENT");
        }

        return order;
    }

    private String normalizeText(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }

    private BigDecimal calculateSubtotal(Order order) {
        List<OrderDetail> details = orderDetailRepository.findByOrderID_Id(order.getId());
        return details.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateCouponDiscount(DiscountCoupon coupon, BigDecimal subtotal) {
        if (coupon == null || subtotal == null || subtotal.compareTo(BigDecimal.ZERO) <= 0) {
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

    private BigDecimal calculateDiscount(Order order, BigDecimal subtotal) {
        if (order.getCouponID() == null) {
            return BigDecimal.ZERO;
        }
        return calculateCouponDiscount(order.getCouponID(), subtotal);
    }

    private void normalizeCoupon(Order order, BigDecimal subtotal) {
        if (order.getCouponID() != null && !isCouponValid(order.getCouponID(), subtotal)) {
            order.setCouponID(null);
        }
    }

    private void recalculateOrder(Order order) {
        BigDecimal subtotal = calculateSubtotal(order);

        normalizeCoupon(order, subtotal);

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

    private PosPromotionGetDto mapPromotionDto(DiscountCoupon coupon, Order order, BigDecimal subtotal) {
        PosPromotionGetDto dto = new PosPromotionGetDto();

        boolean eligible = isCouponValid(coupon, subtotal);
        BigDecimal missingAmount = BigDecimal.ZERO;

        if (coupon.getMinOrderValue() != null && subtotal.compareTo(coupon.getMinOrderValue()) < 0) {
            missingAmount = coupon.getMinOrderValue().subtract(subtotal);
        }

        dto.setCouponId(coupon.getId());
        dto.setCouponCode(coupon.getCouponCode());
        dto.setName(coupon.getName());
        dto.setDescription(coupon.getDescription());
        dto.setDiscountType(coupon.getDiscountType());
        dto.setDiscountValue(coupon.getDiscountValue());
        dto.setMinOrderValue(coupon.getMinOrderValue());
        dto.setMaxDiscountValue(coupon.getMaxDiscountValue());

        dto.setEligible(eligible);
        dto.setApplied(order.getCouponID() != null && order.getCouponID().getId().equals(coupon.getId()));
        dto.setEstimatedDiscount(eligible ? calculateCouponDiscount(coupon, subtotal) : BigDecimal.ZERO);
        dto.setMissingAmount(missingAmount);

        return dto;
    }

    private PosProductColorGetDto mapProductColorDto(ProductColor pc) {
        ProductPriceData priceData = resolveProductPrice(pc);

        PosProductColorGetDto dto = new PosProductColorGetDto();
        dto.setProductColorId(pc.getId());
        dto.setProductName(pc.getProductID().getProductName());
        dto.setColorName(pc.getColorID() != null ? pc.getColorID().getColorName() : null);
        dto.setSizeName(pc.getSizeID() != null ? pc.getSizeID().getSizeName() : null);

        dto.setOriginalPrice(priceData.getOriginalPrice());
        dto.setFinalPrice(priceData.getFinalPrice());
        dto.setDiscountValue(priceData.getDiscountValue());
        dto.setDiscountPercent(priceData.getDiscountPercent());
        dto.setDiscounted(priceData.isDiscounted());
        dto.setDiscountType(priceData.getDiscountType());
        dto.setDiscountLabel(priceData.getDiscountLabel());

        dto.setPrice(priceData.getFinalPrice());
        dto.setStockQuantity(pc.getStockQuantity());

        dto.setDisplayName(
                pc.getProductID().getProductName()
                        + " - " + (pc.getColorID() != null ? pc.getColorID().getColorName() : "-")
                        + " - " + (pc.getSizeID() != null ? pc.getSizeID().getSizeName() : "-")
                        + " - " + formatMoney(priceData.getFinalPrice())
                        + " - Tồn: " + pc.getStockQuantity()
        );

        if (pc.getImages() != null && !pc.getImages().isEmpty()) {
            Image firstImage = pc.getImages().get(0);
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

        Payment latestPayment = paymentRepository.findTopByOrderIDOrderByIdDesc(order).orElse(null);

        PosOrderGetDto dto = new PosOrderGetDto();
        dto.setOrderId(order.getId());
        dto.setOrderDate(order.getOrderDate());
        dto.setOrderType(order.getOrderType());
        dto.setStatus(order.getStatus()); // enum

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
        dto.setTrackingCode(order.getTrackingCode());

        dto.setPaymentMethod(latestPayment != null ? normalizePosPaymentMethod(latestPayment.getMethod()) : null);
        dto.setPaymentStatus(
                latestPayment != null ? latestPayment.getStatus() : null
        );
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

    private static class ProductPriceData {
        private final BigDecimal originalPrice;
        private final BigDecimal finalPrice;
        private final BigDecimal discountValue;
        private final Integer discountPercent;
        private final boolean discounted;
        private final String discountType;
        private final String discountLabel;

        public ProductPriceData(
                BigDecimal originalPrice,
                BigDecimal finalPrice,
                BigDecimal discountValue,
                Integer discountPercent,
                boolean discounted,
                String discountType,
                String discountLabel
        ) {
            this.originalPrice = originalPrice;
            this.finalPrice = finalPrice;
            this.discountValue = discountValue;
            this.discountPercent = discountPercent;
            this.discounted = discounted;
            this.discountType = discountType;
            this.discountLabel = discountLabel;
        }

        public BigDecimal getOriginalPrice() {
            return originalPrice;
        }

        public BigDecimal getFinalPrice() {
            return finalPrice;
        }

        public BigDecimal getDiscountValue() {
            return discountValue;
        }

        public Integer getDiscountPercent() {
            return discountPercent;
        }

        public boolean isDiscounted() {
            return discounted;
        }

        public String getDiscountType() {
            return discountType;
        }

        public String getDiscountLabel() {
            return discountLabel;
        }
    }

    private ProductPriceData resolveProductPrice(ProductColor productColor) {
        BigDecimal originalPrice = productColor.getPrice();

        if (originalPrice == null || originalPrice.compareTo(BigDecimal.ZERO) <= 0) {
            return new ProductPriceData(
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    0,
                    false,
                    null,
                    null
            );
        }

        LocalDate today = LocalDate.now();

        List<ProductDiscount> activeDiscounts = productDiscountRepository.findActiveDiscounts(
                productColor.getId(),
                today
        );

        if (activeDiscounts == null || activeDiscounts.isEmpty()) {
            return new ProductPriceData(
                    originalPrice,
                    originalPrice,
                    BigDecimal.ZERO,
                    0,
                    false,
                    null,
                    null
            );
        }

        return activeDiscounts.stream()
                .map(discount -> calculateDiscountedPrice(originalPrice, discount))
                .min(Comparator.comparing(ProductPriceData::getFinalPrice))
                .orElse(new ProductPriceData(
                        originalPrice,
                        originalPrice,
                        BigDecimal.ZERO,
                        0,
                        false,
                        null,
                        null
                ));
    }

    private ProductPriceData calculateDiscountedPrice(BigDecimal originalPrice, ProductDiscount discount) {
        if (discount == null || originalPrice == null || originalPrice.compareTo(BigDecimal.ZERO) <= 0) {
            return new ProductPriceData(
                    originalPrice,
                    originalPrice,
                    BigDecimal.ZERO,
                    0,
                    false,
                    null,
                    null
            );
        }

        BigDecimal realDiscountValue = BigDecimal.ZERO;
        String discountType = discount.getDiscountType();

        if ("percent".equalsIgnoreCase(discountType)) {
            BigDecimal percent = discount.getDiscountValue() == null
                    ? BigDecimal.ZERO
                    : discount.getDiscountValue();

            realDiscountValue = originalPrice
                    .multiply(percent)
                    .divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP);

            if (discount.getMaxDiscountValue() != null
                    && discount.getMaxDiscountValue().compareTo(BigDecimal.ZERO) > 0
                    && realDiscountValue.compareTo(discount.getMaxDiscountValue()) > 0) {
                realDiscountValue = discount.getMaxDiscountValue();
            }
        } else if ("fixed".equalsIgnoreCase(discountType)) {
            realDiscountValue = discount.getDiscountValue() == null
                    ? BigDecimal.ZERO
                    : discount.getDiscountValue();
        }

        if (realDiscountValue.compareTo(BigDecimal.ZERO) < 0) {
            realDiscountValue = BigDecimal.ZERO;
        }

        if (realDiscountValue.compareTo(originalPrice) > 0) {
            realDiscountValue = originalPrice;
        }

        BigDecimal finalPrice = originalPrice.subtract(realDiscountValue);
        if (finalPrice.compareTo(BigDecimal.ZERO) < 0) {
            finalPrice = BigDecimal.ZERO;
        }

        Integer discountPercent = 0;
        if (originalPrice.compareTo(BigDecimal.ZERO) > 0 && realDiscountValue.compareTo(BigDecimal.ZERO) > 0) {
            discountPercent = realDiscountValue
                    .multiply(BigDecimal.valueOf(100))
                    .divide(originalPrice, 0, RoundingMode.HALF_UP)
                    .intValue();
        }

        String discountLabel = null;
        if ("percent".equalsIgnoreCase(discountType) && discount.getDiscountValue() != null) {
            discountLabel = "-" + discount.getDiscountValue().stripTrailingZeros().toPlainString() + "%";
        } else if ("fixed".equalsIgnoreCase(discountType) && realDiscountValue.compareTo(BigDecimal.ZERO) > 0) {
            discountLabel = "-" + formatMoney(realDiscountValue) + "đ";
        }

        return new ProductPriceData(
                originalPrice,
                finalPrice,
                realDiscountValue,
                discountPercent,
                realDiscountValue.compareTo(BigDecimal.ZERO) > 0,
                discountType,
                discountLabel
        );
    }

    private String normalizePosPaymentMethod(String method) {
        if (!StringUtils.hasText(method)) return null;

        String normalized = method.trim().toUpperCase();
        return switch (normalized) {
            case "CASH", "COD" -> "CASH";
            case "BANKING", "BANK", "TRANSFER", "BANK_TRANSFER", "MB", "MBBANK", "MB BANK" -> "BANKING";
            default -> normalized;
        };
    }

    private String normalizePosPaymentStatus(String status) {
        if (!StringUtils.hasText(status)) return null;

        String normalized = status.trim().toUpperCase();
        return switch (normalized) {
            case "SUCCESS", "PAID" -> "PAID";
            case "UNPAID", "PENDING_PAYMENT" -> "UNPAID";
            case "CANCELLED", "CANCELED" -> "CANCELLED";
            default -> normalized;
        };
    }
}