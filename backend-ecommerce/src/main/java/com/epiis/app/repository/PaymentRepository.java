package com.epiis.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.epiis.app.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, String> {

    Optional<Payment> findByTransactionId(String transactionId);

    Optional<Payment> findByOrder_IdOrder(String idOrder);

    List<Payment> findByStatus(String status);

    List<Payment> findByOrder_IdOrderIn(List<String> orderIds);
}
