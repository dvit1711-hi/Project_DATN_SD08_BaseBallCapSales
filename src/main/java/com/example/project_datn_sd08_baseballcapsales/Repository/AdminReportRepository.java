package com.example.project_datn_sd08_baseballcapsales.Repository;

import com.example.project_datn_sd08_baseballcapsales.Model.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface AdminReportRepository extends JpaRepository<Order, Integer> {

    interface StaffOptionProjection {
        Integer getEmployeeId();
        String getUsername();
        String getEmail();
    }

    interface StaffTodayProjection {
        Integer getEmployeeId();
        String getEmployeeName();
        String getEmail();
        Long getTotalOrdersToday();
        Long getTotalProductsToday();
        BigDecimal getTotalRevenueToday();
    }

    interface CustomerSummaryProjection {
        Integer getCustomerId();
        String getCustomerName();
        String getCustomerPhone();
        String getCustomerType();
        Long getTotalOrders();
        BigDecimal getTotalSpent();
    }

    interface PurchaseHistoryRowProjection {
        Integer getOrderId();
        String getTrackingCode();
        LocalDateTime getOrderDate();
        String getOrderType();
        String getOrderStatus();

        Integer getCustomerId();
        String getCustomerName();
        String getCustomerPhone();

        Integer getEmployeeId();
        String getEmployeeName();

        BigDecimal getTotalAmount();
        String getPaymentMethod();
        String getPaymentStatus();

        Integer getOrderDetailId();
        Integer getProductColorId();
        String getProductName();
        String getColorName();
        String getSizeName();
        Integer getQuantity();
        BigDecimal getPrice();
    }

    @Query(value = """
            SELECT
                a.accountID AS employeeId,
                a.username AS username,
                a.email AS email
            FROM Accounts a
            JOIN AccountRoles ar ON ar.accountID = a.accountID
            JOIN Roles r ON r.roleID = ar.roleID
            WHERE r.roleName = 'ROLE_STAFF'
            ORDER BY a.username
            """, nativeQuery = true)
    List<StaffOptionProjection> findStaffOptions();

    @Query(value = """
            SELECT
                a.accountID AS employeeId,
                a.username AS employeeName,
                a.email AS email,
                COALESCE(x.totalOrdersToday, 0) AS totalOrdersToday,
                COALESCE(y.totalProductsToday, 0) AS totalProductsToday,
                COALESCE(x.totalRevenueToday, 0) AS totalRevenueToday
            FROM Accounts a
            JOIN AccountRoles ar ON ar.accountID = a.accountID
            JOIN Roles r ON r.roleID = ar.roleID
            LEFT JOIN (
                SELECT
                    o.employeeID,
                    COUNT(*) AS totalOrdersToday,
                    SUM(o.totalAmount) AS totalRevenueToday
                FROM Orders o
                WHERE o.orderType = 'OFFLINE'
                  AND CAST(o.orderDate AS DATE) = :reportDate
                  AND UPPER(ISNULL(o.status, '')) NOT IN ('CANCELLED', 'PENDING')
                GROUP BY o.employeeID
            ) x ON x.employeeID = a.accountID
            LEFT JOIN (
                SELECT
                    o.employeeID,
                    SUM(od.quantity) AS totalProductsToday
                FROM Orders o
                JOIN OrderDetails od ON od.orderID = o.orderID
                WHERE o.orderType = 'OFFLINE'
                  AND CAST(o.orderDate AS DATE) = :reportDate
                  AND UPPER(ISNULL(o.status, '')) NOT IN ('CANCELLED', 'PENDING')
                GROUP BY o.employeeID
            ) y ON y.employeeID = a.accountID
            WHERE r.roleName = 'ROLE_STAFF'
              AND (:employeeId IS NULL OR a.accountID = :employeeId)
            ORDER BY a.username
            """, nativeQuery = true)
    List<StaffTodayProjection> findStaffTodayOverview(
            @Param("employeeId") Integer employeeId,
            @Param("reportDate") LocalDate reportDate
    );

    @Query(value = """
            SELECT
                o.accountID AS customerId,
                COALESCE(a.username, o.customerName, N'Khách lẻ') AS customerName,
                COALESCE(a.phoneNumber, o.customerPhone, '') AS customerPhone,
                CASE WHEN o.accountID IS NULL THEN 'WALK_IN' ELSE 'ACCOUNT' END AS customerType,
                COUNT(DISTINCT o.orderID) AS totalOrders,
                COALESCE(SUM(o.totalAmount), 0) AS totalSpent
            FROM Orders o
            LEFT JOIN Accounts a ON a.accountID = o.accountID
            WHERE o.orderType = 'OFFLINE'
              AND CAST(o.orderDate AS DATE) = :reportDate
              AND UPPER(ISNULL(o.status, '')) NOT IN ('CANCELLED', 'PENDING')
              AND (:employeeId IS NULL OR o.employeeID = :employeeId)
              AND (
                    :keyword = ''
                    OR CAST(o.orderID AS NVARCHAR(50)) LIKE CONCAT('%', :keyword, '%')
                    OR COALESCE(o.trackingCode, '') LIKE CONCAT('%', :keyword, '%')
                    OR COALESCE(a.username, '') LIKE CONCAT('%', :keyword, '%')
                    OR COALESCE(a.phoneNumber, '') LIKE CONCAT('%', :keyword, '%')
                    OR COALESCE(o.customerName, '') LIKE CONCAT('%', :keyword, '%')
                    OR COALESCE(o.customerPhone, '') LIKE CONCAT('%', :keyword, '%')
                  )
            GROUP BY
                o.accountID,
                COALESCE(a.username, o.customerName, N'Khách lẻ'),
                COALESCE(a.phoneNumber, o.customerPhone, ''),
                CASE WHEN o.accountID IS NULL THEN 'WALK_IN' ELSE 'ACCOUNT' END
            ORDER BY totalSpent DESC, totalOrders DESC
            """, nativeQuery = true)
    List<CustomerSummaryProjection> findCustomerSummary(
            @Param("keyword") String keyword,
            @Param("employeeId") Integer employeeId,
            @Param("reportDate") LocalDate reportDate
    );

    @Query(value = """
            SELECT
                o.orderID AS orderId,
                o.trackingCode AS trackingCode,
                o.orderDate AS orderDate,
                o.orderType AS orderType,
                o.status AS orderStatus,

                o.accountID AS customerId,
                COALESCE(a.username, o.customerName, N'Khách lẻ') AS customerName,
                COALESCE(a.phoneNumber, o.customerPhone, '') AS customerPhone,

                e.accountID AS employeeId,
                e.username AS employeeName,

                o.totalAmount AS totalAmount,
                p.method AS paymentMethod,
                p.status AS paymentStatus,

                od.orderDetailsID AS orderDetailId,
                pc.productColorID AS productColorId,
                pr.productName AS productName,
                c.colorName AS colorName,
                s.sizeName AS sizeName,
                od.quantity AS quantity,
                od.price AS price
            FROM Orders o
            LEFT JOIN Accounts a ON a.accountID = o.accountID
            LEFT JOIN Accounts e ON e.accountID = o.employeeID
            LEFT JOIN Payments p ON p.orderID = o.orderID
            LEFT JOIN OrderDetails od ON od.orderID = o.orderID
            LEFT JOIN ProductColors pc ON pc.productColorID = od.productColorID
            LEFT JOIN Products pr ON pr.productID = pc.productID
            LEFT JOIN Colors c ON c.colorID = pc.colorID
            LEFT JOIN Sizes s ON s.sizeID = pc.sizeID
            WHERE o.orderType = 'OFFLINE'
              AND CAST(o.orderDate AS DATE) = :reportDate
              AND UPPER(ISNULL(o.status, '')) NOT IN ('CANCELLED', 'PENDING')
              AND (:employeeId IS NULL OR o.employeeID = :employeeId)
              AND (
                    :keyword = ''
                    OR CAST(o.orderID AS NVARCHAR(50)) LIKE CONCAT('%', :keyword, '%')
                    OR COALESCE(o.trackingCode, '') LIKE CONCAT('%', :keyword, '%')
                    OR COALESCE(a.username, '') LIKE CONCAT('%', :keyword, '%')
                    OR COALESCE(a.phoneNumber, '') LIKE CONCAT('%', :keyword, '%')
                    OR COALESCE(o.customerName, '') LIKE CONCAT('%', :keyword, '%')
                    OR COALESCE(o.customerPhone, '') LIKE CONCAT('%', :keyword, '%')
                  )
            ORDER BY o.orderDate DESC, o.orderID DESC, od.orderDetailsID ASC
            """, nativeQuery = true)
    List<PurchaseHistoryRowProjection> findPurchaseHistoryRows(
            @Param("keyword") String keyword,
            @Param("employeeId") Integer employeeId,
            @Param("reportDate") LocalDate reportDate
    );
}