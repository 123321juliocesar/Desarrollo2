package com.epiis.app.controller.reqresp.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddToCartRequest {

    @NotBlank(message = "El ID del usuario es obligatorio")
    private String idUser;

    @NotBlank(message = "El ID del producto es obligatorio")
    private String idProduct;

    @NotBlank(message = "El ID de la variante es obligatorio")
    private String idVariant;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer quantity;
}
