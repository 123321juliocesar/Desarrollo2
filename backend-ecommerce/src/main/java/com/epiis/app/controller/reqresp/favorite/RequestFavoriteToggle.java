package com.epiis.app.controller.reqresp.favorite;

import com.epiis.app.dto.DtoFavorite;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestFavoriteToggle {

    private Dto dto;

    @Getter
    @Setter
    public static class Dto {
        private DtoFavorite favorite;
    }
}
