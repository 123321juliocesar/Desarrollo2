package com.epiis.app.entity;

import java.math.BigDecimal;

import com.epiis.app.generic.EntityGeneric;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tCartItem")
public class CartItem extends EntityGeneric {

    @Id
    @Column(name = "idCartItem", length = 36, nullable = false)
    private String idCartItem;

    @ManyToOne
    @JoinColumn(name = "idCart", nullable = false)
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "idProduct", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "idVariant", nullable = false)
    private ProductVariant variant;

    @Column(name = "quantity", nullable = false)
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer quantity;

    @Column(name = "unitPrice", precision = 10, scale = 2, nullable = false)
    private BigDecimal unitPrice;
}
