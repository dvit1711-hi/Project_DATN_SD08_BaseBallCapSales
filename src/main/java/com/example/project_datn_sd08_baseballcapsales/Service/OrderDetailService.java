package com.example.project_datn_sd08_baseballcapsales.Service;

import com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto.GetOrderDetailDto;
import com.example.project_datn_sd08_baseballcapsales.Repository.OrderDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderDetailService {
    @Autowired
    private OrderDetailRepository orderDetailRepository;

    public List<GetOrderDetailDto> getAllOrderDetails() {
        return orderDetailRepository
                .findAll()
                .stream()
                .map(GetOrderDetailDto::new)
                .toList();
    }


}
