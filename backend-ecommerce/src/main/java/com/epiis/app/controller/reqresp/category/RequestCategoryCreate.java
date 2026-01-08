package com.epiis.app.controller.reqresp.category;

import com.epiis.app.dto.DtoCategory;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestCategoryCreate {
    @Getter
    @Setter
    public class Dto {
        private DtoCategory category;
    }

    private Dto dto = new Dto();
}
