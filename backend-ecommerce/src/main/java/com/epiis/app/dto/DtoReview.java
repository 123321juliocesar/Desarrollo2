package com.epiis.app.dto;

import com.epiis.app.generic.DtoGeneric;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoReview extends DtoGeneric {
    private String idReview;
    private String idUser;
    private String userName; // To display who commented
    private String idProduct;
    private Integer rating;
    private String comment;
}
