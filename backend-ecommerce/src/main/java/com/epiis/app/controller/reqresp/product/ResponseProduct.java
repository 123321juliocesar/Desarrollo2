package com.epiis.app.controller.reqresp.product;

import com.epiis.app.dto.DtoProduct;
import com.epiis.app.generic.ResponseGeneric;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseProduct extends ResponseGeneric {
    private DtoProduct product;
}
