package com.epiis.app.controller.reqresp.brand;

import java.util.List;

import com.epiis.app.dto.DtoBrand;
import com.epiis.app.generic.ResponseGeneric;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseBrandList extends ResponseGeneric {
    private List<DtoBrand> brands;
}
