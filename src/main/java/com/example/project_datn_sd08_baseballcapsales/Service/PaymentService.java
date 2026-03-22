package com.example.project_datn_sd08_baseballcapsales.Service;

import com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto.GetPaidOrderWithDetailsDto;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.*;
import com.example.project_datn_sd08_baseballcapsales.Repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

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
    private AccountRepository accountRepository;

    //    tien mat
    @Transactional
    public Order checkoutCOD(Integer accountId) {
        return checkout(accountId, "COD");
    }

    @Transactional
    public Order checkout(Integer accountId, String method) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản"));
        Cart cart = cartRepository.findByAccountID_Id(accountId)
                .orElse(null);
        if (cart == null) {
            throw new RuntimeException("Không tìm thấy giỏ hàng");
        }
        List<CartItem> cartItems =
                cartItemRepository.findByCartID_Id(cart.getId());
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Giỏ hàng trống");
        }
        BigDecimal total = BigDecimal.ZERO;
        Order order = new Order();
        order.setAccountID(account);
        order.setStatus("PENDING_PAYMENT");
        order = orderRepository.save(order);
        for (CartItem item : cartItems) {
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