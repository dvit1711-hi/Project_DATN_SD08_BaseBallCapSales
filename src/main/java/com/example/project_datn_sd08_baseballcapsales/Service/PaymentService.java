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

    Cart cart = cartRepository.findByAccountID_Id(accountId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy giỏ hàng"));

    List<CartItem> cartItems = cartItemRepository.findByCartID_Id(cart.getId());

    if (cartItems.isEmpty()) {
        throw new RuntimeException("Giỏ hàng trống");
    }

    BigDecimal total = BigDecimal.ZERO;

    // tính tiền + check stock
    for (CartItem item : cartItems) {
        ProductColor pc = item.getProductColorID();

        if (item.getQuantity() > pc.getStockQuantity()) {
            throw new RuntimeException("Không đủ hàng");
        }

        BigDecimal price = pc.getProductID().getPrice();
        total = total.add(price.multiply(BigDecimal.valueOf(item.getQuantity())));
    }

    // tạo order
    Order order = new Order();
    order.setAccountID(account);
    order.setStatus("CONFIRMED");
    order.setTotalAmount(total);
    order = orderRepository.save(order);

    // tạo order detail + trừ stock
    for (CartItem item : cartItems) {
        ProductColor pc = item.getProductColorID();

        OrderDetail detail = new OrderDetail();
        detail.setOrderID(order);
        detail.setProductColorID(pc);
        detail.setQuantity(item.getQuantity());
        detail.setPrice(pc.getProductID().getPrice());

        orderDetailRepository.save(detail);

        // trừ kho
        pc.setStockQuantity(pc.getStockQuantity() - item.getQuantity());
    }

    // payment
    Payment payment = new Payment();
    payment.setOrderID(order);
    payment.setAmount(total);
    payment.setMethod("COD");
    payment.setStatus("UNPAID");
    paymentRepository.save(payment);

    // clear cart
    cartItemRepository.deleteAll(cartItems);

    return order;
}
}