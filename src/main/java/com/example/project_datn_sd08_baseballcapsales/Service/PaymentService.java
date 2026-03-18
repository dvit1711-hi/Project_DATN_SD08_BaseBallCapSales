package com.example.project_datn_sd08_baseballcapsales.Service;

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
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản"));
        Cart cart = cartRepository.findByAccountID_Id(accountId);

        List<CartItem> cartItems =
                cartItemRepository.findByCartID_Id(cart.getId());
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Giỏ hàng trống");
        }
        BigDecimal total = BigDecimal.ZERO;
        Order order = new Order();
        order.setAccountID(account);
        order.setStatus("CONFIRMED");
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
        Payment payment = new Payment();
        payment.setOrderID(order);
        payment.setAmount(total);
        payment.setMethod("COD");
        payment.setStatus("UNPAID");
        paymentRepository.save(payment);
        cartItemRepository.deleteAll(cartItems);
        return order;
    }
}