package com.epiis.app.controller.reqresp.cart;

import java.math.BigDecimal;
import java.util.List;

import com.epiis.app.dto.DtoCart;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoCartResponse extends DtoCart {
    private List<DtoCartItemWithDetails> items;
    private Integer totalItems;
    private BigDecimal subtotal;
    private BigDecimal shippingCost;
    private BigDecimal total;
}
