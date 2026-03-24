package com.example.project_datn_sd08_baseballcapsales.Repository;

import com.example.project_datn_sd08_baseballcapsales.Model.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {
    List<OrderDetail> findByOrderID_Id(Integer orderId);

    @Query("select od.productColorID.productID.id, sum(od.quantity)\n" +
            "    from OrderDetail od\n" +
            "    group by od.productColorID.productID.id\n" +
            "    order by sum(od.quantity) desc")
    List<Object[]> topProductsByQuantity();

    @Query("select od.productColorID.productID.brandID.name,\n" +
            "               sum(od.quantity)\n" +
            "        from OrderDetail od\n" +
            "        group by od.productColorID.productID.brandID.name\n" +
            "        order by sum(od.quantity) desc")
    List<Object[]> topBrandsByQuantity();
}
