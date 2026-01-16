package com.epiis.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.epiis.app.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, String> {

    List<OrderItem> findByOrder_IdOrder(String idOrder);

    void deleteByOrder_IdOrder(String idOrder);
}
