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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.epiis.app.business.BrandBusiness;
import com.epiis.app.controller.reqresp.brand.RequestBrandCreate;
import com.epiis.app.controller.reqresp.brand.RequestBrandUpdate;
import com.epiis.app.controller.reqresp.brand.ResponseBrand;
import com.epiis.app.controller.reqresp.brand.ResponseBrandList;
import com.epiis.app.dto.DtoBrand;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(path = "/brand")
@Tag(name = "Brand", description = "API de gestión de marcas")
public class BrandController {

    @Autowired
    private BrandBusiness brandBusiness;

    @PostMapping(path = "/create")
    @Operation(summary = "Crear marca", description = "Crea una nueva marca en el sistema")
    public ResponseEntity<ResponseBrand> create(@RequestBody RequestBrandCreate request) {
        ResponseBrand response = new ResponseBrand();

        String result = this.brandBusiness.create(request.getDto().getBrand());

        if (result.equals("Marca creada correctamente")) {
            response.success();
            response.listMessage.add(result);
            response.setBrand(request.getDto().getBrand());
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else {
            response.error();
            response.listMessage.add(result);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(path = "/list")
    @Operation(summary = "Listar marcas", description = "Obtiene todas las marcas del sistema")
    public ResponseEntity<ResponseBrandList> list() {
        ResponseBrandList response = new ResponseBrandList();

        List<DtoBrand> brands = this.brandBusiness.findAll();

        response.success();
        response.setBrands(brands);
        response.listMessage.add("Marcas obtenidas correctamente");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(path = "/{id}")
    @Operation(summary = "Obtener marca", description = "Obtiene una marca por su ID")
    public ResponseEntity<ResponseBrand> getById(@PathVariable("id") String id) {
        ResponseBrand response = new ResponseBrand();

        DtoBrand brand = this.brandBusiness.findById(id);

        if (brand != null) {
            response.success();
            response.setBrand(brand);
            response.listMessage.add("Marca encontrada");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.error();
            response.listMessage.add("Marca no encontrada");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping(path = "/update/{id}")
    @Operation(summary = "Actualizar marca", description = "Actualiza una marca existente")
    public ResponseEntity<ResponseBrand> update(
            @PathVariable("id") String id,
            @RequestBody RequestBrandUpdate request) {
        ResponseBrand response = new ResponseBrand();

        String result = this.brandBusiness.update(id, request.getDto().getBrand());

        if (result.equals("Marca actualizada correctamente")) {
            response.success();
            response.listMessage.add(result);
            DtoBrand updatedBrand = this.brandBusiness.findById(id);
            response.setBrand(updatedBrand);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.error();
            response.listMessage.add(result);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping(path = "/delete/{id}")
    @Operation(summary = "Eliminar marca", description = "Elimina una marca del sistema")
    public ResponseEntity<ResponseBrand> delete(@PathVariable("id") String id) {
        ResponseBrand response = new ResponseBrand();

        String result = this.brandBusiness.delete(id);

        if (result.equals("Marca eliminada correctamente")) {
            response.success();
            response.listMessage.add(result);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.error();
            response.listMessage.add(result);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(path = "/search")
    @Operation(summary = "Buscar marcas", description = "Busca marcas por nombre")
    public ResponseEntity<ResponseBrandList> search(@RequestParam("name") String name) {
        ResponseBrandList response = new ResponseBrandList();

        List<DtoBrand> brands = this.brandBusiness.search(name);

        response.success();
        response.setBrands(brands);
        response.listMessage.add("Búsqueda completada");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
