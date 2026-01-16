package com.epiis.app.controller.reqresp.order;

import java.util.List;

import com.epiis.app.dto.DtoOrder;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoOrderResponse extends DtoOrder {

    // Items de la orden
    private List<DtoOrderItemResponse> items;
    private Integer totalItems;
    private String userName;
}
