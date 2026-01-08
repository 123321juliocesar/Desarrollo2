package com.epiis.app.controller.reqresp.category;

import java.util.List;

import com.epiis.app.dto.DtoCategory;
import com.epiis.app.generic.ResponseGeneric;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseCategoryList extends ResponseGeneric {
    private List<DtoCategory> categories;
}
