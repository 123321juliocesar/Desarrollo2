package com.epiis.app.controller.reqresp.productvariant;

import com.epiis.app.dto.DtoProductVariant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestProductVariantCreate {

    private Dto dto;

    @Getter
    @Setter
    public static class Dto {
        private DtoProductVariant productVariant;
    }
}
