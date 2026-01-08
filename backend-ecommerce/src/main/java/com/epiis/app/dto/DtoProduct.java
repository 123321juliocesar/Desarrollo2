package com.epiis.app.dto;

import java.math.BigDecimal;

import com.epiis.app.generic.DtoGeneric;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoProduct extends DtoGeneric {

    private String idProduct;
    private String idCategory;
    private String idBrand;
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal oldPrice;
    private String imageUrl;
    private String status;
    private BigDecimal averageRating;
    private Integer reviewCount;
}
