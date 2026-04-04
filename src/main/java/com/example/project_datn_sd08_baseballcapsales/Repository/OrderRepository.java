package com.example.project_datn_sd08_baseballcapsales.Repository;

import com.example.project_datn_sd08_baseballcapsales.Model.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByAccountID_Id(Integer accountId);

    long countByEmployeeID_IdAndOrderTypeIgnoreCaseAndStatusIgnoreCase(
            Integer employeeId,
            String orderType,
            String status
    );

    List<Order> findByEmployeeID_IdAndOrderTypeIgnoreCaseAndStatusIgnoreCaseOrderByOrderDateDesc(
            Integer employeeId,
            String orderType,
            String status
    );

    Optional<Order> findByIdAndEmployeeID_Id(Integer orderId, Integer employeeId);
    
    @Query("SELECT o FROM Order o WHERE o.accountID.id = :accountId AND EXISTS (SELECT p FROM Payment p WHERE p.orderID = o AND p.status = 'PAID')")
    List<Order> findPaidOrdersByAccountId(@Param("accountId") Integer accountId);

    @Query("select coalesce(sum(o.totalAmount), 0) from Order o")
    BigDecimal getTotalRevenue();

    @Query("select o.status, count(o) from Order o group by o.status")
    List<Object[]> countByStatus();

    @Query("select coalesce(avg(o.totalAmount), 0) from Order o")
    BigDecimal getAverageOrderValue();

    @Query(value = "SELECT FORMAT(orderDate, 'yyyy-MM-dd') AS date, COUNT(*) AS amount FROM Orders GROUP BY FORMAT(orderDate, 'yyyy-MM-dd') ORDER BY date", nativeQuery = true)
    List<Object[]> countByDay();

    @Query(value = "SELECT FORMAT(orderDate, 'yyyy-MM') AS month, COUNT(*) AS amount FROM Orders GROUP BY FORMAT(orderDate, 'yyyy-MM') ORDER BY month", nativeQuery = true)
    List<Object[]> countByMonth();
}
