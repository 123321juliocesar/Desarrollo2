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

import com.epiis.app.business.CategoryBusiness;
import com.epiis.app.controller.reqresp.category.RequestCategoryCreate;
import com.epiis.app.controller.reqresp.category.RequestCategoryUpdate;
import com.epiis.app.controller.reqresp.category.ResponseCategory;
import com.epiis.app.controller.reqresp.category.ResponseCategoryList;
import com.epiis.app.dto.DtoCategory;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(path = "/category")
@Tag(name = "Category", description = "API de gestión de categorías")
public class CategoryController {

    @Autowired
    private CategoryBusiness categoryBusiness;

    @PostMapping(path = "/create")
    @Operation(summary = "Crear categoría", description = "Crea una nueva categoría en el sistema")
    public ResponseEntity<ResponseCategory> create(@RequestBody RequestCategoryCreate request) {
        ResponseCategory response = new ResponseCategory();

        String result = this.categoryBusiness.create(request.getDto().getCategory());

        if (result.equals("Categoría creada correctamente")) {
            response.success();
            response.listMessage.add(result);
            response.setCategory(request.getDto().getCategory());
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else {
            response.error();
            response.listMessage.add(result);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(path = "/list")
    @Operation(summary = "Listar categorías", description = "Obtiene todas las categorías del sistema")
    public ResponseEntity<ResponseCategoryList> list() {
        ResponseCategoryList response = new ResponseCategoryList();

        List<DtoCategory> categories = this.categoryBusiness.findAll();

        response.success();
        response.setCategories(categories);
        response.listMessage.add("Categorías obtenidas correctamente");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(path = "/{id}")
    @Operation(summary = "Obtener categoría", description = "Obtiene una categoría por su ID")
    public ResponseEntity<ResponseCategory> getById(@PathVariable("id") String id) {
        ResponseCategory response = new ResponseCategory();

        DtoCategory category = this.categoryBusiness.findById(id);

        if (category != null) {
            response.success();
            response.setCategory(category);
            response.listMessage.add("Categoría encontrada");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.error();
            response.listMessage.add("Categoría no encontrada");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping(path = "/update/{id}")
    @Operation(summary = "Actualizar categoría", description = "Actualiza una categoría existente")
    public ResponseEntity<ResponseCategory> update(
            @PathVariable("id") String id,
            @RequestBody RequestCategoryUpdate request) {
        ResponseCategory response = new ResponseCategory();

        String result = this.categoryBusiness.update(id, request.getDto().getCategory());

        if (result.equals("Categoría actualizada correctamente")) {
            response.success();
            response.listMessage.add(result);
            DtoCategory updatedCategory = this.categoryBusiness.findById(id);
            response.setCategory(updatedCategory);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.error();
            response.listMessage.add(result);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping(path = "/delete/{id}")
    @Operation(summary = "Eliminar categoría", description = "Elimina una categoría del sistema")
    public ResponseEntity<ResponseCategory> delete(@PathVariable("id") String id) {
        ResponseCategory response = new ResponseCategory();

        String result = this.categoryBusiness.delete(id);

        if (result.equals("Categoría eliminada correctamente")) {
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
    @Operation(summary = "Buscar categorías", description = "Busca categorías por nombre")
    public ResponseEntity<ResponseCategoryList> search(@RequestParam("name") String name) {
        ResponseCategoryList response = new ResponseCategoryList();

        List<DtoCategory> categories = this.categoryBusiness.search(name);

        response.success();
        response.setCategories(categories);
        response.listMessage.add("Búsqueda completada");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
