package com.example.project_datn_sd08_baseballcapsales.Repository;

import com.example.project_datn_sd08_baseballcapsales.Model.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {
    List<OrderDetail> findByOrderID_Id(Integer orderId);
}
