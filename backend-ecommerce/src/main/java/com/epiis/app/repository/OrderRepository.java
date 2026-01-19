package com.epiis.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.epiis.app.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderRepository extends JpaRepository<Order, String> {

    Optional<Order> findByOrderNumber(String orderNumber);

    // List<Order> findByUser_IdUser(String idUser);
    @EntityGraph(attributePaths = { "user", "orderItems", "orderItems.product", "orderItems.variant",
            "orderItems.product.brand", "orderItems.product.category" })
    @Query("SELECT DISTINCT o FROM Order o WHERE o.user.idUser = :idUser ORDER BY o.orderDate DESC")
    List<Order> findByUser_IdUserOrderByOrderDateDesc(@Param("idUser") String idUser);

    @EntityGraph(attributePaths = { "user", "orderItems", "orderItems.product", "orderItems.variant",
            "orderItems.product.brand", "orderItems.product.category" })
    @Query("SELECT DISTINCT o FROM Order o WHERE o.idOrder = :idOrder")
    Optional<Order> findByIdOrder(@Param("idOrder") String idOrder);

    @EntityGraph(attributePaths = { "user", "orderItems", "orderItems.product", "orderItems.variant",
            "orderItems.product.brand", "orderItems.product.category" })
    @Query("SELECT DISTINCT o FROM Order o WHERE o.status = :status")
    List<Order> findByStatus(@Param("status") String status);

    @EntityGraph(attributePaths = { "user", "orderItems", "orderItems.product", "orderItems.variant",
                        "orderItems.product.brand", "orderItems.product.category" })
        @Query("SELECT DISTINCT o FROM Order o WHERE o.status = :status")
        Page<Order> findByStatus(@Param("status") String status, Pageable pageable);

    @EntityGraph(attributePaths = { "user", "orderItems", "orderItems.product", "orderItems.variant",
            "orderItems.product.brand", "orderItems.product.category" })
    @Query("SELECT DISTINCT o FROM Order o")
    Page<Order> findAll(Pageable pageable);

    @Override
    @EntityGraph(attributePaths = { "user", "orderItems", "orderItems.product", "orderItems.variant",
            "orderItems.product.brand", "orderItems.product.category" })
    @Query("SELECT DISTINCT o FROM Order o")
    List<Order> findAll();
    
    long countByStatus(String status);

    @EntityGraph(attributePaths = { "user", "orderItems", "orderItems.product", "orderItems.variant", "orderItems.product.brand", "orderItems.product.category" })
        Page<Order> findByStatusNot(String status, Pageable pageable);
        
    @Query("SELECT COUNT(o) > 0 FROM Order o JOIN o.orderItems oi WHERE o.user.idUser = :userId AND oi.product.idProduct = :productId AND LOWER(o.status) = 'delivered'")
    boolean existsByUserIdAndProductId(@Param("userId") String userId, @Param("productId") String productId);
}
