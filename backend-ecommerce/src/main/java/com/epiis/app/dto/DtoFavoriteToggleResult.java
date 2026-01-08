package com.epiis.app.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoFavoriteToggleResult {

    private String action; // "added" o "removed"
    private String message;
    private DtoFavorite favorite; // Solo presente si action = "added"
    private String error; // Solo presente si hay error
}
