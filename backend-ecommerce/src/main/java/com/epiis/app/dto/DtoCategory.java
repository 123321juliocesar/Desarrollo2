package com.epiis.app.dto;

import com.epiis.app.generic.DtoGeneric;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoCategory extends DtoGeneric {
    private String idCategory;
    private String name;
    private String description;
    private Integer productCount;
}
