package com.epiis.app.controller.reqresp.favorite;

import com.epiis.app.dto.DtoFavorite;
import com.epiis.app.generic.ResponseGeneric;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseFavorite extends ResponseGeneric {
    private DtoFavorite favorite;
    private String action; // "added" o "removed"
}
