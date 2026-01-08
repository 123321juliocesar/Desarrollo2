package com.epiis.app.controller.reqresp.role;

import com.epiis.app.dto.DtoRole;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestRoleUpdate {
    @Getter
    @Setter
    public class Dto {
        private DtoRole role;
    }

    private Dto dto = new Dto();
}
