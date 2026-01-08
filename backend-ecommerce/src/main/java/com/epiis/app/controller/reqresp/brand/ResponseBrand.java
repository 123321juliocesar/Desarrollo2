package com.epiis.app.controller.reqresp.brand;

import com.epiis.app.dto.DtoBrand;
import com.epiis.app.generic.ResponseGeneric;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseBrand extends ResponseGeneric {
    private DtoBrand brand;
}
