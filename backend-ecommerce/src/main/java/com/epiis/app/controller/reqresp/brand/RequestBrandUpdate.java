package com.epiis.app.controller.reqresp.brand;

import com.epiis.app.dto.DtoBrand;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestBrandUpdate {
    @Getter
    @Setter
    public class Dto {
        private DtoBrand brand;
    }

    private Dto dto = new Dto();
}
