package com.epiis.app.controller.reqresp.productvariant;

import com.epiis.app.dto.DtoProductVariant;
import com.epiis.app.generic.ResponseGeneric;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseProductVariant extends ResponseGeneric {
    private DtoProductVariant productVariant;
}
