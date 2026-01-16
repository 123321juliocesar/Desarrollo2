package com.epiis.app.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.epiis.app.generic.DtoGeneric;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoOrder extends DtoGeneric {

    private String idOrder;
    private String idUser;
    private String orderNumber;
    private Timestamp orderDate;
    private BigDecimal totalAmount;
    private BigDecimal subtotal;
    private BigDecimal shippingCost;
    private String status;
    private String shippingAddress;
    private String shippingCity;
    private String shippingPostalCode;
    private String shippingProvince;
    private String shippingCountry;
}
