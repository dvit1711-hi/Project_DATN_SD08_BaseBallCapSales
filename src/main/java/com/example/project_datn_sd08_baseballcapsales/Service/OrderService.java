package com.example.project_datn_sd08_baseballcapsales.Service;

import com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto.GetOrderDto;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.Order;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.Payment;
import com.example.project_datn_sd08_baseballcapsales.Model.enums.OrderStatus;
import com.example.project_datn_sd08_baseballcapsales.Model.enums.PaymentStatus;
import com.example.project_datn_sd08_baseballcapsales.Repository.OrderRepository;
import com.example.project_datn_sd08_baseballcapsales.Repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private PaymentRepository paymentRepository;

    public List<GetOrderDto> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(GetOrderDto::new)
                .toList();
    }

    //Xác nhận đơn
    public Order confirmOrder(Integer orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow();

        if (order.getStatus() != OrderStatus.PENDING_CONFIRM &&
                order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new RuntimeException("Không thể xác nhận");
        }

        order.setStatus(OrderStatus.CONFIRMED);
        return orderRepository.save(order);
    }


    //Bắt đầu giao
    public Order startShipping(Integer orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow();

        if (order.getStatus() != OrderStatus.CONFIRMED) {
            throw new RuntimeException("Chưa xác nhận");
        }

        order.setStatus(OrderStatus.SHIPPING);
        return orderRepository.save(order);
    }


    //Giao thành công
    public Order deliverOrder(Integer orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow();

        if (order.getStatus() != OrderStatus.SHIPPING) {
            throw new RuntimeException("Chưa giao");
        }

        order.setStatus(OrderStatus.DELIVERED);
        return orderRepository.save(order);
    }

    //Thanh toán (COD hoặc BANK)
    public Order completeOrder(Integer orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow();

        Payment payment = paymentRepository.findByOrderID(order);

        payment.setStatus(PaymentStatus.PAID);

        order.setStatus(OrderStatus.COMPLETED);

        paymentRepository.save(payment);
        return orderRepository.save(order);
    }
}
