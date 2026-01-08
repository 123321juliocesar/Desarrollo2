package com.epiis.app.controller.reqresp.user;

import com.epiis.app.dto.DtoUser;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestUserUpdate {
    @Getter
    @Setter
    public class Dto {
        private DtoUser user;
    }

    private Dto dto = new Dto();
}
