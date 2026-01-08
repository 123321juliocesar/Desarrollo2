package com.epiis.app.dto;

import com.epiis.app.generic.DtoGeneric;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoRole extends DtoGeneric {
    private String idRole;
    private String name;
    private String description;
}
