package com.epiis.app.dto;

import com.epiis.app.generic.DtoGeneric;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoProductVariant extends DtoGeneric {

    private String idVariant;
    private String idProduct;
    private String size;
    private String color;
    private String colorHex;
    private Integer stock;
}
