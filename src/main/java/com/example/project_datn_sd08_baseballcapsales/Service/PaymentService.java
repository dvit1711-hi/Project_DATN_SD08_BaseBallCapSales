package com.example.project_datn_sd08_baseballcapsales.Service;

import com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto.GetPaidOrderWithDetailsDto;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.*;
import com.example.project_datn_sd08_baseballcapsales.Repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@Service
public class PaymentService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductColorRepository productColorRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private DiscountCouponRepository discountCouponRepository;

    //    tien mat
    @Transactional
    public Order checkoutCOD(Integer accountId) {
        return checkout(accountId, "COD");
    }

    @Transactional
    public Order checkout(Integer accountId, String method) {
        return checkout(accountId, method, null, null);
    }

    @Transactional
    public Order checkout(Integer accountId, String method, List<Integer> selectedCartItemIds) {
        return checkout(accountId, method, selectedCartItemIds, null);
    }

    @Transactional
    public Order checkout(Integer accountId, String method, List<Integer> selectedCartItemIds, String couponCode) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản"));
        Cart cart = cartRepository.findByAccountID_Id(accountId)
                .orElse(null);
        if (cart == null) {
            throw new RuntimeException("Không tìm thấy giỏ hàng");
        }
        List<CartItem> allCartItems =
                cartItemRepository.findByCartID_Id(cart.getId());

        if (allCartItems.isEmpty()) {
            throw new RuntimeException("Giỏ hàng trống");
        }

        List<CartItem> cartItems;
        if (selectedCartItemIds == null || selectedCartItemIds.isEmpty()) {
            cartItems = allCartItems;
        } else {
            Set<Integer> selectedSet = new HashSet<>(selectedCartItemIds);
            cartItems = allCartItems.stream()
                    .filter(item -> selectedSet.contains(item.getId()))
                    .toList();

            if (cartItems.size() != selectedSet.size()) {
                throw new RuntimeException("Có sản phẩm không thuộc giỏ hàng hiện tại");
            }
        }

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Vui lòng chọn ít nhất 1 sản phẩm để thanh toán");
        }

        BigDecimal subTotal = BigDecimal.ZERO;
        Order order = new Order();
        order.setAccountID(account);
        order.setStatus("PENDING_PAYMENT");
        Address address = addressRepository.findByAccount_Id(accountId);
        order.setShippingAddress(formatAddressSnapshot(address));
        order = orderRepository.save(order);
        for (CartItem item : cartItems) {
            ProductColor productColor = item.getProductColorID();
            if (productColor == null) {
                throw new RuntimeException("Sản phẩm không hợp lệ trong giỏ hàng");
            }

            Integer quantity = item.getQuantity();
            if (quantity == null || quantity <= 0) {
                throw new RuntimeException("Số lượng sản phẩm không hợp lệ");
            }

            Integer currentStock = productColor.getStockQuantity() == null ? 0 : productColor.getStockQuantity();
            if (currentStock < quantity) {
                String productName = productColor.getProductID() != null
                        ? productColor.getProductID().getProductName()
                        : "Sản phẩm";
                throw new RuntimeException(productName + " không đủ tồn kho");
            }

            productColor.setStockQuantity(currentStock - quantity);
            productColorRepository.save(productColor);

            BigDecimal price = item.getProductColorID().getPrice();
            subTotal = subTotal.add(
                    price.multiply(BigDecimal.valueOf(item.getQuantity()))
            );
            OrderDetail detail = new OrderDetail();
            detail.setOrderID(order);
            detail.setProductColorID(item.getProductColorID());
            detail.setQuantity(item.getQuantity());
            detail.setPrice(price);
            orderDetailRepository.save(detail);
        }

        DiscountCoupon appliedCoupon = null;
        BigDecimal discountAmount = BigDecimal.ZERO;
        if (couponCode != null && !couponCode.trim().isEmpty()) {
            appliedCoupon = validateCoupon(couponCode.trim(), subTotal);
            discountAmount = calculateDiscountAmount(appliedCoupon, subTotal);
            Integer currentQuantity = appliedCoupon.getQuantity() == null ? 0 : appliedCoupon.getQuantity();
            appliedCoupon.setQuantity(Math.max(currentQuantity - 1, 0));
            discountCouponRepository.save(appliedCoupon);
            order.setCouponID(appliedCoupon);
        }

        BigDecimal total = subTotal.subtract(discountAmount).max(BigDecimal.ZERO);
        order.setTotalAmount(total);
        orderRepository.save(order);

        String normalizedMethod = method == null ? "COD" : method.trim().toUpperCase();
        if (!List.of("COD", "BANK_TRANSFER", "E_WALLET").contains(normalizedMethod)) {
            throw new RuntimeException("Phương thức thanh toán không hợp lệ");
        }

        Payment payment = new Payment();
        payment.setOrderID(order);
        payment.setAmount(total);
        payment.setMethod(normalizedMethod);
        payment.setStatus("UNPAID");
        paymentRepository.save(payment);
        cartItemRepository.deleteAll(cartItems);
        return order;
    }

    public List<GetPaidOrderWithDetailsDto> getOrdersWithDetailsForAccount(Integer accountId) {
        List<Order> orders = orderRepository.findByAccountID_Id(accountId);
        return orders.stream()
                .map(this::mapOrderToDetailsDto)
                .toList();
    }

    public List<GetPaidOrderWithDetailsDto> getAllOrdersWithDetails() {
        return orderRepository.findAll()
                .stream()
                .map(this::mapOrderToDetailsDto)
                .toList();
    }

    @Transactional
    public Payment confirmPayment(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        Payment payment = paymentRepository.findByOrderID(order)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thanh toán của đơn hàng"));

        payment.setStatus("PAID");
        order.setStatus("PAID");
        orderRepository.save(order);
        return paymentRepository.save(payment);
    }

    @Transactional
    public Order cancelOrderForAccount(Integer accountId, Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        if (order.getAccountID() == null || !order.getAccountID().getId().equals(accountId)) {
            throw new RuntimeException("Bạn không có quyền hủy đơn hàng này");
        }

        return cancelOrderInternal(order);
    }

    @Transactional
    public Order cancelOrderByAdmin(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
        return cancelOrderInternal(order);
    }

    private GetPaidOrderWithDetailsDto mapOrderToDetailsDto(Order order) {
        var paymentOpt = paymentRepository.findByOrderID(order);
        String paymentStatus = paymentOpt
                .map(Payment::getStatus)
                .orElse("UNKNOWN");
        String paymentMethod = paymentOpt
                .map(Payment::getMethod)
                .orElse("UNKNOWN");

        List<OrderDetail> orderDetails = orderDetailRepository.findByOrderID_Id(order.getId());
        return new GetPaidOrderWithDetailsDto(order, paymentStatus, paymentMethod, orderDetails);
    }

    private String formatAddressSnapshot(Address address) {
        if (address == null) {
            return null;
        }

        return Stream.of(
                        address.getUnitNumber(),
                        address.getStreetNumber(),
                        address.getAddressLine1(),
                        address.getAddressLine2(),
                        address.getCity(),
                        address.getRegion(),
                        address.getPostalCode()
                )
                .filter(part -> part != null && !part.trim().isEmpty())
                .map(String::trim)
                .reduce((a, b) -> a + ", " + b)
                .orElse(null);
    }

    private Order cancelOrderInternal(Order order) {
        String currentOrderStatus = order.getStatus() == null ? "" : order.getStatus().trim().toUpperCase();
        if ("CANCELLED".equals(currentOrderStatus)) {
            throw new RuntimeException("Đơn hàng đã được hủy trước đó");
        }

        Payment payment = paymentRepository.findByOrderID(order).orElse(null);
        String paymentStatus = payment != null && payment.getStatus() != null
                ? payment.getStatus().trim().toUpperCase()
                : "";

        if ("PAID".equals(paymentStatus) || "PAID".equals(currentOrderStatus)) {
            throw new RuntimeException("Không thể hủy đơn hàng đã thanh toán");
        }

        List<OrderDetail> details = orderDetailRepository.findByOrderID_Id(order.getId());
        for (OrderDetail detail : details) {
            ProductColor productColor = detail.getProductColorID();
            if (productColor == null) {
                continue;
            }

            Integer currentStock = productColor.getStockQuantity() == null ? 0 : productColor.getStockQuantity();
            Integer quantity = detail.getQuantity() == null ? 0 : detail.getQuantity();
            productColor.setStockQuantity(currentStock + Math.max(quantity, 0));
            productColorRepository.save(productColor);
        }

        DiscountCoupon coupon = order.getCouponID();
        if (coupon != null) {
            Integer currentQuantity = coupon.getQuantity() == null ? 0 : coupon.getQuantity();
            coupon.setQuantity(currentQuantity + 1);
            discountCouponRepository.save(coupon);
        }

        order.setStatus("CANCELLED");
        orderRepository.save(order);

        if (payment != null) {
            payment.setStatus("CANCELLED");
            paymentRepository.save(payment);
        }

        return order;
    }

    private DiscountCoupon validateCoupon(String couponCode, BigDecimal orderAmount) {
        DiscountCoupon coupon = discountCouponRepository.findByCouponCodeIgnoreCase(couponCode)
                .orElseThrow(() -> new RuntimeException("Mã giảm giá không tồn tại"));

        if (Boolean.FALSE.equals(coupon.getActive())) {
            throw new RuntimeException("Mã giảm giá đã bị tắt");
        }

        LocalDate now = LocalDate.now();
        if (coupon.getStartDate() != null && now.isBefore(coupon.getStartDate())) {
            throw new RuntimeException("Mã giảm giá chưa đến thời gian áp dụng");
        }
        if (coupon.getEndDate() != null && now.isAfter(coupon.getEndDate())) {
            throw new RuntimeException("Mã giảm giá đã hết hạn");
        }

        Integer quantity = coupon.getQuantity() == null ? 0 : coupon.getQuantity();
        if (quantity <= 0) {
            throw new RuntimeException("Mã giảm giá đã hết lượt sử dụng");
        }

        BigDecimal minOrder = coupon.getMinOrderValue() == null ? BigDecimal.ZERO : coupon.getMinOrderValue();
        if (orderAmount.compareTo(minOrder) < 0) {
            throw new RuntimeException("Đơn hàng chưa đạt giá trị tối thiểu để dùng mã giảm giá");
        }

        return coupon;
    }

    private BigDecimal calculateDiscountAmount(DiscountCoupon coupon, BigDecimal orderAmount) {
        if (coupon.getDiscountValue() == null || coupon.getDiscountValue().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Giá trị giảm giá không hợp lệ");
        }

        String type = coupon.getDiscountType() == null ? "" : coupon.getDiscountType().trim().toLowerCase();
        BigDecimal discount;

        if ("percent".equals(type)) {
            discount = orderAmount
                    .multiply(coupon.getDiscountValue())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            if (coupon.getMaxDiscountValue() != null && coupon.getMaxDiscountValue().compareTo(BigDecimal.ZERO) > 0) {
                discount = discount.min(coupon.getMaxDiscountValue());
            }
        } else if ("fixed".equals(type)) {
            discount = coupon.getDiscountValue();
        } else {
            throw new RuntimeException("Loại mã giảm giá không hợp lệ");
        }

        return discount.max(BigDecimal.ZERO).min(orderAmount);
    }
}