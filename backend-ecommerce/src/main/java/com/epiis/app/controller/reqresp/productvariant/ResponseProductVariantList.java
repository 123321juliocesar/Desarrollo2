package com.epiis.app.controller.reqresp.productvariant;

import java.util.List;

import com.epiis.app.dto.DtoProductVariant;
import com.epiis.app.generic.ResponseGeneric;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseProductVariantList extends ResponseGeneric {
    private List<DtoProductVariant> productVariants;
}
