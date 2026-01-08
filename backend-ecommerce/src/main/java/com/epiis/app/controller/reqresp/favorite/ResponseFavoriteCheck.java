package com.epiis.app.controller.reqresp.favorite;

import com.epiis.app.generic.ResponseGeneric;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseFavoriteCheck extends ResponseGeneric {
    private boolean favorite;
}
