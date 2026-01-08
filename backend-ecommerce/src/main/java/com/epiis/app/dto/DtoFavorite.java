package com.epiis.app.dto;

import com.epiis.app.generic.DtoGeneric;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoFavorite extends DtoGeneric {
    private String idFavorite;
    private String idUser;
    private String idProduct;
}
