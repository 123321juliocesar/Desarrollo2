package com.epiis.app.dto;

import com.epiis.app.generic.DtoGeneric;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoCart extends DtoGeneric{
    private String idCart;
    private String idUser;
}
