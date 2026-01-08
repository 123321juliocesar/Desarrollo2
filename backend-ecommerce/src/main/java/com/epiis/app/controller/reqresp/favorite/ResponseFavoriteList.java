package com.epiis.app.controller.reqresp.favorite;

import java.util.List;

import com.epiis.app.dto.DtoFavoriteWithProduct;
import com.epiis.app.generic.ResponseGeneric;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseFavoriteList extends ResponseGeneric {
    private List<DtoFavoriteWithProduct> favorites;
}
