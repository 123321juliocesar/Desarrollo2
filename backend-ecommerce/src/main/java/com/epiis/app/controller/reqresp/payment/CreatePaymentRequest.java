package com.epiis.app.controller.reqresp.payment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePaymentRequest {

    private String idOrder;
    private String returnUrl;
    private String cancelUrl;
}
