package com.epiis.app.dto;

import java.math.BigDecimal;

import com.epiis.app.generic.DtoGeneric;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoOrderItem extends DtoGeneric {

    private String idOrderItem;
    private String idOrder;
    private String idProduct;
    private String idVariant;
    private Integer quantity;
    private BigDecimal unitPrice;
}
