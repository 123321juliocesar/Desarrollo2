package com.epiis.app.controller.reqresp.role;

import com.epiis.app.dto.DtoRole;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestRoleCreate {
    @Getter
    @Setter
    public class Dto {
        private DtoRole role;
    }

    private Dto dto = new Dto();
}
