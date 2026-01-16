package com.epiis.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.epiis.app.entity.Order;

public interface OrderRepository extends JpaRepository<Order, String> {

    Optional<Order> findByOrderNumber(String orderNumber);

    //List<Order> findByUser_IdUser(String idUser);
    @EntityGraph(attributePaths = { "user", "orderItems", "orderItems.product", "orderItems.variant", "orderItems.product.brand", "orderItems.product.category" })
    @Query("SELECT DISTINCT o FROM Order o WHERE o.user.idUser = :idUser ORDER BY o.orderDate DESC")
    List<Order> findByUser_IdUserOrderByOrderDateDesc(@Param("idUser") String idUser);

    @EntityGraph(attributePaths = { "user", "orderItems", "orderItems.product", "orderItems.variant", "orderItems.product.brand", "orderItems.product.category" })
    @Query("SELECT DISTINCT o FROM Order o WHERE o.idOrder = :idOrder")
    Optional<Order> findByIdOrder(@Param("idOrder") String idOrder);

    @EntityGraph(attributePaths = { "user", "orderItems", "orderItems.product", "orderItems.variant", "orderItems.product.brand", "orderItems.product.category" })
    @Query("SELECT DISTINCT o FROM Order o WHERE o.status = :status")
    List<Order> findByStatus(@Param("status") String status);

    @Override
    @EntityGraph(attributePaths = { "user", "orderItems", "orderItems.product", "orderItems.variant", "orderItems.product.brand", "orderItems.product.category" })
    @Query("SELECT DISTINCT o FROM Order o")
    List<Order> findAll();
}
