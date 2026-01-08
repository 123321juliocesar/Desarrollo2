package com.epiis.app.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoProductDetail extends DtoProduct {
    private List<DtoProductVariant> variants;
}
