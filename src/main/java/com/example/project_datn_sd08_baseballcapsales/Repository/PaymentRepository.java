package com.example.project_datn_sd08_baseballcapsales.Repository;

import com.example.project_datn_sd08_baseballcapsales.Model.entity.Order;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    Optional<Payment> findByOrderID(Order order);
    
    Optional<Payment> findByOrderID_IdAndStatus(Integer orderId, String status);

    @Query("select p.method, count(p) from Payment p group by p.method")
    List<Object[]> countByMethod();

    @Query("select p.status, count(p) from Payment p group by p.status")
    List<Object[]> countByStatus();

    @Query("select coalesce(sum(p.amount), 0) from Payment p where p.status = 'SUCCESS'")
    BigDecimal totalSuccessfulPayment();
}
