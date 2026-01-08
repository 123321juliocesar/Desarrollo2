package com.epiis.app.controller.reqresp.role;

import java.util.List;

import com.epiis.app.dto.DtoRole;
import com.epiis.app.generic.ResponseGeneric;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseRoleList extends ResponseGeneric {
    private List<DtoRole> roles;
}
