package com.epiis.app.controller.reqresp.order;

import com.epiis.app.dto.DtoOrderItem;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoOrderItemResponse extends DtoOrderItem {
    // Detalles del producto
    private String productName;
    private String productImageUrl;

    // Detalles de la variante
    private String variantSize;
    private String variantColor;
    private String variantColorHex;
}
