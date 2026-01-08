package com.epiis.app.controller.reqresp.role;

import com.epiis.app.dto.DtoRole;
import com.epiis.app.generic.ResponseGeneric;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseRole extends ResponseGeneric {
    private DtoRole role;
}
