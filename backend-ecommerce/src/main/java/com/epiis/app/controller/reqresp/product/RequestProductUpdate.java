package com.epiis.app.controller.reqresp.product;

import java.util.List;

import com.epiis.app.dto.DtoProduct;
import com.epiis.app.dto.DtoProductVariant;

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
        private List<DtoProductVariant> variants;
    }
}
