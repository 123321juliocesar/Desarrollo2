package com.epiis.app.entity;

import java.math.BigDecimal;

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
@Table(name = "tOrderItem")
@Getter
@Setter
public class OrderItem extends EntityGeneric {

    @Id
    @Column(name = "idOrderItem", length = 36, nullable = false)
    private String idOrderItem;

    @ManyToOne
    @JoinColumn(name = "idOrder", nullable = false)
    private Order order;

    @ManyToOne
    @JoinColumn(name = "idProduct", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "idVariant")
    private ProductVariant variant;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "unitPrice", precision = 10, scale = 2, nullable = false)
    private BigDecimal unitPrice;
}
