package com.epiis.app.repository;

import com.epiis.app.entity.Order;
import com.epiis.app.dto.DtoSalesReport.DtoProductPerformance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface SalesRepository extends JpaRepository<Order, String> {

        @Query("SELECT new com.epiis.app.dto.DtoSalesReport$DtoProductPerformance(" +
                        "oi.product.name, SUM(oi.quantity), SUM(oi.unitPrice * oi.quantity)) " +
                        "FROM OrderItem oi " +
                        "WHERE oi.order.orderDate BETWEEN :startDate AND :endDate " +
                        "AND oi.order.status IN ('confirmed', 'shipped', 'delivered') " +
                        "GROUP BY oi.product.name " +
                        "ORDER BY SUM(oi.quantity) DESC")
        List<DtoProductPerformance> getProductPerformance(@Param("startDate") Date startDate,
                        @Param("endDate") Date endDate, Pageable pageable);

        @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.orderDate BETWEEN :startDate AND :endDate AND o.status IN ('confirmed', 'shipped', 'delivered')")
        BigDecimal getTotalSales(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

        @Query("SELECT COUNT(o) FROM Order o WHERE o.orderDate BETWEEN :startDate AND :endDate AND o.status IN ('confirmed', 'shipped', 'delivered')")
        Long getTotalOrders(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

        @Query("SELECT o FROM Order o JOIN FETCH o.user WHERE o.orderDate BETWEEN :startDate AND :endDate AND o.status IN ('confirmed', 'shipped', 'delivered') ORDER BY o.orderDate DESC")
        Page<Order> getOrdersInRange(@Param("startDate") Date startDate, @Param("endDate") Date endDate, Pageable pageable);
}
