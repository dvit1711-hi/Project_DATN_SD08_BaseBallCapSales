package com.example.project_datn_sd08_baseballcapsales.Service;

import com.example.project_datn_sd08_baseballcapsales.Model.entity.*;
import com.example.project_datn_sd08_baseballcapsales.Repository.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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
    private CartItemRepository cartItemRepository;

    @Autowired
    private AccountRepository accountRepository;

//    tien mat
    @Transactional
    public Order checkoutCOD(Integer accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản"));
        List<CartItem> cartItems =
                cartItemRepository.findByCartID(accountId);
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Giỏ hàng trống");
        }
        BigDecimal total = BigDecimal.ZERO;
        Order order = new Order();
        order.setAccountID(account);
        order.setStatus("CONFIRMED");
        order = orderRepository.save(order);
        for (CartItem item : cartItems) {
            BigDecimal price = item.getProductID().getPrice();
            total = total.add(
                    price.multiply(BigDecimal.valueOf(item.getQuantity()))
            );

            OrderDetail detail = new OrderDetail();
            detail.setOrderID(order);
            detail.setProductID(item.getProductID());
            detail.setQuantity(item.getQuantity());
            detail.setPrice(price);

            orderDetailRepository.save(detail);
        }
        order.setTotalAmount(total);
        orderRepository.save(order);
        Payment payment = new Payment();
        payment.setOrderID(order);
        payment.setAmount(total);
        payment.setMethod("COD");
        payment.setStatus("UNPAID");
        paymentRepository.save(payment);
        cartItemRepository.deleteAll(cartItems);
        return order;
    }

    @Transactional
    public String markOnlineSuccess(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        Payment payment = paymentRepository.findByOrderID(order)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thanh toán"));
        order.setStatus("PAID");
        payment.setStatus("PAID");
        orderRepository.save(order);
        paymentRepository.save(payment);

        return "SUCCESS";
    }
}