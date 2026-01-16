package com.epiis.app.dto;

import java.math.BigDecimal;
import com.epiis.app.generic.DtoGeneric;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoCartItem extends DtoGeneric{
    private String idCartItem;
    private String idCart;
    private String idProduct;
    private String idVariant;
    private Integer quantity;
    private BigDecimal unitPrice;
}
