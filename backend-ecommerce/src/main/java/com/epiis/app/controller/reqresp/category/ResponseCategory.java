package com.epiis.app.controller.reqresp.category;

import com.epiis.app.dto.DtoCategory;
import com.epiis.app.generic.ResponseGeneric;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseCategory extends ResponseGeneric {
    private DtoCategory category;
}
