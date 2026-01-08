package com.epiis.app.controller.reqresp.user;

import com.epiis.app.generic.ResponseGeneric;

import com.epiis.app.dto.DtoUser;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseUserRegister extends ResponseGeneric {
    private DtoUser user;
}
