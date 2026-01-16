package com.epiis.app.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.epiis.app.generic.DtoGeneric;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoPayment extends DtoGeneric {
    private String idPayment;
    private String idOrder;
    private String paymentMethod;
    private String status;
    private String transactionId;
    private Timestamp paymentDate;
    private BigDecimal amount;
}
