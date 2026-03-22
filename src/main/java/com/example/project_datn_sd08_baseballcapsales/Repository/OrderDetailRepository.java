package com.example.project_datn_sd08_baseballcapsales.Repository;

import com.example.project_datn_sd08_baseballcapsales.Model.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {

    @Query("select od.product.id, sum(od.quantity) from OrderDetail od group by od.product.id order by sum(od.quantity) desc")
    List<Object[]> topProductsByQuantity();

    @Query("select od.product.brandID.name, sum(od.quantity) from OrderDetail od group by od.product.brandID.name order by sum(od.quantity) desc")
    List<Object[]> topBrandsByQuantity();
}
