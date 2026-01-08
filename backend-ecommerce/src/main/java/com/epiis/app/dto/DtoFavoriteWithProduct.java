package com.epiis.app.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoFavoriteWithProduct extends DtoFavorite {
    private DtoProduct product;
}
