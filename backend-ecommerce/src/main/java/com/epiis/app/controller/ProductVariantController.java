package com.epiis.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epiis.app.business.ProductVariantBusiness;
import com.epiis.app.controller.reqresp.productvariant.RequestProductVariantCreate;
import com.epiis.app.controller.reqresp.productvariant.RequestProductVariantUpdate;
import com.epiis.app.controller.reqresp.productvariant.ResponseProductVariant;
import com.epiis.app.controller.reqresp.productvariant.ResponseProductVariantList;
import com.epiis.app.dto.DtoProductVariant;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(path = "/product-variant")
@Tag(name = "ProductVariant", description = "API de gestión de variantes de productos")
public class ProductVariantController {

    @Autowired
    private ProductVariantBusiness productVariantBusiness;

    @PostMapping(path = "/create")
    @Operation(summary = "Crear variante", description = "Crea una nueva variante de producto")
    public ResponseEntity<ResponseProductVariant> create(@RequestBody RequestProductVariantCreate request) {
        ResponseProductVariant response = new ResponseProductVariant();

        String result = this.productVariantBusiness.create(request.getDto().getProductVariant());

        if (result.equals("Variante creada correctamente")) {
            response.success();
            response.listMessage.add(result);
            response.setProductVariant(request.getDto().getProductVariant());
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else {
            response.error();
            response.listMessage.add(result);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(path = "/list")
    @Operation(summary = "Listar variantes", description = "Obtiene todas las variantes del sistema")
    public ResponseEntity<ResponseProductVariantList> list() {
        ResponseProductVariantList response = new ResponseProductVariantList();

        List<DtoProductVariant> variants = this.productVariantBusiness.findAll();

        response.success();
        response.setProductVariants(variants);
        response.listMessage.add("Variantes obtenidas correctamente");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(path = "/{id}")
    @Operation(summary = "Obtener variante", description = "Obtiene una variante por su ID")
    public ResponseEntity<ResponseProductVariant> getById(@PathVariable("id") String id) {
        ResponseProductVariant response = new ResponseProductVariant();

        DtoProductVariant variant = this.productVariantBusiness.findById(id);

        if (variant != null) {
            response.success();
            response.setProductVariant(variant);
            response.listMessage.add("Variante encontrada");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.error();
            response.listMessage.add("Variante no encontrada");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping(path = "/update/{id}")
    @Operation(summary = "Actualizar variante", description = "Actualiza una variante existente")
    public ResponseEntity<ResponseProductVariant> update(
            @PathVariable("id") String id,
            @RequestBody RequestProductVariantUpdate request) {
        ResponseProductVariant response = new ResponseProductVariant();

        String result = this.productVariantBusiness.update(id, request.getDto().getProductVariant());

        if (result.equals("Variante actualizada correctamente")) {
            response.success();
            response.listMessage.add(result);
            DtoProductVariant updatedVariant = this.productVariantBusiness.findById(id);
            response.setProductVariant(updatedVariant);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.error();
            response.listMessage.add(result);
            response.setProductVariant(request.getDto().getProductVariant());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping(path = "/delete/{id}")
    @Operation(summary = "Eliminar variante", description = "Elimina una variante del sistema")
    public ResponseEntity<ResponseProductVariant> delete(@PathVariable("id") String id) {
        ResponseProductVariant response = new ResponseProductVariant();

        String result = this.productVariantBusiness.delete(id);

        if (result.equals("Variante eliminada correctamente")) {
            response.success();
            response.listMessage.add(result);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.error();
            response.listMessage.add(result);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(path = "/product/{idProduct}")
    @Operation(summary = "Variantes por producto", description = "Obtiene todas las variantes de un producto")
    public ResponseEntity<ResponseProductVariantList> getByProduct(@PathVariable("idProduct") String idProduct) {
        ResponseProductVariantList response = new ResponseProductVariantList();

        List<DtoProductVariant> variants = this.productVariantBusiness.findByProduct(idProduct);

        response.success();
        response.setProductVariants(variants);
        response.listMessage.add("Variantes obtenidas correctamente");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
