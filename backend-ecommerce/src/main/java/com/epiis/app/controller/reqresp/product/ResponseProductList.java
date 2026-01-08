package com.epiis.app.controller.reqresp.product;

import java.util.List;

import com.epiis.app.dto.DtoProduct;
import com.epiis.app.generic.ResponseGeneric;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseProductList extends ResponseGeneric {
    private List<DtoProduct> products;
}
