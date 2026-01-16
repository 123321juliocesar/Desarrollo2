package com.epiis.app.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import com.epiis.app.generic.EntityGeneric;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tOrder")
@Getter
@Setter
public class Order extends EntityGeneric {

    @Id
    @Column(name = "idOrder", length = 36, nullable = false)
    private String idOrder;

    @ManyToOne
    @JoinColumn(name = "idUser", nullable = false)
    private User user;

    @Column(name = "orderNumber", length = 50, nullable = false, unique = true)
    private String orderNumber;

    @Column(name = "orderDate", nullable = false)
    private Timestamp orderDate;

    @Column(name = "totalAmount", precision = 10, scale = 2, nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "subtotal", precision = 10, scale = 2, nullable = false)
    private BigDecimal subtotal;

    @Column(name = "shippingCost", precision = 10, scale = 2, nullable = false)
    private BigDecimal shippingCost;

    @Column(name = "status", length = 50, nullable = false)
    private String status;

    @Column(name = "shippingAddress", length = 255)
    private String shippingAddress;

    @Column(name = "shippingCity", length = 100)
    private String shippingCity;

    @Column(name = "shippingPostalCode", length = 20)
    private String shippingPostalCode;

    @Column(name = "shippingProvince", length = 100)
    private String shippingProvince;

    @Column(name = "shippingCountry", length = 100)
    private String shippingCountry;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems;
}
