package com.epiis.app.controller.reqresp.product;

import com.epiis.app.dto.DtoProduct;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestProductUpdate {

    private Dto dto;

    @Getter
    @Setter
    public static class Dto {
        private DtoProduct product;
    }
}
