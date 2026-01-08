package com.epiis.app.controller.reqresp.user;

import com.epiis.app.dto.DtoUser;
import com.epiis.app.generic.ResponseGeneric;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseUserLogin extends ResponseGeneric {
	private DtoUser user;
}
