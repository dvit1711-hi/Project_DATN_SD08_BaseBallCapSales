package com.example.project_datn_sd08_baseballcapsales.Service;

import com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto.GetOrderDto;
import com.example.project_datn_sd08_baseballcapsales.Repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    public List<GetOrderDto> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(GetOrderDto::new)
                .toList();
    }
}
