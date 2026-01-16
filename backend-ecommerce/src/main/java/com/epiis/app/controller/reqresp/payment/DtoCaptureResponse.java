package com.epiis.app.controller.reqresp.payment;

import com.epiis.app.dto.DtoPayment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoCaptureResponse extends DtoPayment {

    private String paypalOrderId;
    private String captureId;
}
