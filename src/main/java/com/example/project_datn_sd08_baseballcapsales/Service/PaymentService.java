package com.example.project_datn_sd08_baseballcapsales.Service;

import com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto.GetPaidOrderWithDetailsDto;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.*;
import com.example.project_datn_sd08_baseballcapsales.Repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    //    tien mat
    @Transactional
    public Order checkoutCOD(Integer accountId) {
        return checkout(accountId, "COD");
    }

    @Transactional
    public Order checkout(Integer accountId, String method) {
        return checkout(accountId, method, null);
    }

    @Transactional
    public Order checkout(Integer accountId, String method, List<Integer> selectedCartItemIds) {
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

        BigDecimal total = BigDecimal.ZERO;
        Order order = new Order();
        order.setAccountID(account);
        order.setStatus("PENDING_PAYMENT");
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

            BigDecimal price = item.getProductColorID().getProductID().getPrice();
            total = total.add(
                    price.multiply(BigDecimal.valueOf(item.getQuantity()))
            );
            OrderDetail detail = new OrderDetail();
            detail.setOrderID(order);
            detail.setProductColorID(item.getProductColorID());
            detail.setQuantity(item.getQuantity());
            detail.setPrice(price);
            orderDetailRepository.save(detail);
        }
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
}