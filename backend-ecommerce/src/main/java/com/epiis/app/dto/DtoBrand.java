package com.epiis.app.dto;

import com.epiis.app.generic.DtoGeneric;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoBrand extends DtoGeneric {
    private String idBrand;
    private String name;
}
