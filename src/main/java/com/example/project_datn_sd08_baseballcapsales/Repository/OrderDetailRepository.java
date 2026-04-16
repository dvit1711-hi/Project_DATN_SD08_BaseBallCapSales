package com.example.project_datn_sd08_baseballcapsales.Repository;

import com.example.project_datn_sd08_baseballcapsales.Model.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {
    List<OrderDetail> findByOrderID_Id(Integer orderId);

    Optional<OrderDetail> findByOrderID_IdAndProductColorID_Id(Integer orderId, Integer productColorId);

    @Query("select od.productColorID.productID.id, sum(od.quantity) " +
            "from OrderDetail od " +
            "group by od.productColorID.productID.id " +
            "order by sum(od.quantity) desc")
    List<Object[]> topProductsByQuantity();

    @Query("select od.productColorID.productID.brandID.name, sum(od.quantity) " +
            "from OrderDetail od " +
            "group by od.productColorID.productID.brandID.name " +
            "order by sum(od.quantity) desc")
    List<Object[]> topBrandsByQuantity();

    boolean existsByProductColorID_Id(Integer productColorId);

    boolean existsByOrderID_Id(Integer orderId);

    @Query("""
        select count(od) > 0
        from OrderDetail od
        join od.orderID o
        join od.productColorID pc
        join pc.productID p
        where o.accountID.id = :accountId
          and p.id = :productId
    """)
    boolean existsPurchasedProductForAccount(
            @Param("accountId") Integer accountId,
            @Param("productId") Integer productId
    );
}