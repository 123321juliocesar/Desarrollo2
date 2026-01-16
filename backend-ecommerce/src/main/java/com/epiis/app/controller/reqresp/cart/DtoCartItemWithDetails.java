package com.epiis.app.controller.reqresp.cart;

import com.epiis.app.dto.DtoCartItem;
import com.epiis.app.dto.DtoProduct;
import com.epiis.app.dto.DtoProductVariant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoCartItemWithDetails extends DtoCartItem {
    private DtoProduct product;
    private DtoProductVariant variant;
}
