package com.epiis.app.controller.reqresp.order;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateOrderRequest {
    private String idUser;
    private String shippingAddress;
    private String shippingCity;
    private String shippingPostalCode;
    private String shippingProvince;
    private String shippingCountry;
}
