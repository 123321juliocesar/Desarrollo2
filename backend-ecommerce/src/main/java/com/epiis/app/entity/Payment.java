package com.epiis.app.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.epiis.app.generic.EntityGeneric;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tPayment")
@Getter
@Setter
public class Payment extends EntityGeneric {

    @Id
    @Column(name = "idPayment", length = 36, nullable = false)
    private String idPayment;

    @ManyToOne
    @JoinColumn(name = "idOrder", nullable = false)
    private Order order;

    @Column(name = "paymentMethod", length = 50, nullable = false)
    private String paymentMethod;

    @Column(name = "status", length = 50, nullable = false)
    private String status;

    @Column(name = "transactionId", length = 255)
    private String transactionId;

    @Column(name = "paymentDate", nullable = false)
    private Timestamp paymentDate;

    @Column(name = "amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal amount;
}
