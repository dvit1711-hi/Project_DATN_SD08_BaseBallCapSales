package com.example.project_datn_sd08_baseballcapsales.Repository;

import com.example.project_datn_sd08_baseballcapsales.Model.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByAccountID_Id(Integer accountId);
    
    List<Order> findByAccountID_IdAndStatus(Integer accountId, String status);
    
    @Query("SELECT o FROM Order o WHERE o.accountID.id = :accountId AND EXISTS (SELECT p FROM Payment p WHERE p.orderID = o AND p.status = 'PAID')")
    List<Order> findPaidOrdersByAccountId(@Param("accountId") Integer accountId);
}
