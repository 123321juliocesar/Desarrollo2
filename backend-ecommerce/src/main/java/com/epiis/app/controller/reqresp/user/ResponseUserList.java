package com.epiis.app.controller.reqresp.user;

import java.util.List;

import com.epiis.app.dto.DtoUser;
import com.epiis.app.generic.ResponseGeneric;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseUserList extends ResponseGeneric {
    private List<DtoUser> users;
}
