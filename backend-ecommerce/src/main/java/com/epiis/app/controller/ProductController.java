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

import com.epiis.app.business.ProductBusiness;
import com.epiis.app.controller.reqresp.product.RequestProductCreate;
import com.epiis.app.controller.reqresp.product.RequestProductUpdate;
import com.epiis.app.controller.reqresp.product.ResponseProduct;
import com.epiis.app.controller.reqresp.product.ResponseProductList;
import com.epiis.app.dto.DtoProduct;
import com.epiis.app.dto.DtoProductDetail;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(path = "/product")
@Tag(name = "Product", description = "API de gestión de productos")
public class ProductController {

    @Autowired
    private ProductBusiness productBusiness;

    @PostMapping(path = "/create")
    @Operation(summary = "Crear producto", description = "Crea un nuevo producto en el sistema")
    public ResponseEntity<ResponseProduct> create(@RequestBody RequestProductCreate request) {
        ResponseProduct response = new ResponseProduct();

        //String result = this.productBusiness.create(request.getDto().getProduct());
        String result = this.productBusiness.create(request.getDto().getProduct(), request.getDto().getVariants());


        if (result.equals("Producto creado correctamente")) {
            response.success();
            response.listMessage.add(result);
            response.setProduct(request.getDto().getProduct());
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else {
            response.error();
            response.listMessage.add(result);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(path = "/list")
    @Operation(summary = "Listar productos", description = "Obtiene productos con filtros opcionales (solo activos)")
    public ResponseEntity<ResponseProductList> list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String idCategory,
            @RequestParam(required = false) String idBrand,
            @RequestParam(required = false) String sizes,
            @RequestParam(required = false) String colors,
            @RequestParam(required = false) java.math.BigDecimal minPrice,
            @RequestParam(required = false) java.math.BigDecimal maxPrice,
            @RequestParam(required = false) String sortBy) {
        ResponseProductList response = new ResponseProductList();

        List<DtoProduct> products = this.productBusiness.findWithFilters(
                search, idCategory, idBrand, sizes, colors, minPrice, maxPrice, sortBy);

        response.success();
        response.setProducts(products);
        response.listMessage.add("Productos obtenidos correctamente");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(path = "/{id}")
    @Operation(summary = "Obtener producto con variantes", description = "Obtiene un producto por su ID con todas sus variantes")
    public ResponseEntity<ResponseProduct> getById(@PathVariable("id") String id) {
        ResponseProduct response = new ResponseProduct();

        DtoProductDetail productDetail = this.productBusiness.findByIdWithVariants(id);

        if (productDetail != null) {
            response.success();
            response.setProduct(productDetail);
            response.listMessage.add("Producto encontrado");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.error();
            response.listMessage.add("Producto no encontrado");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping(path = "/update/{id}")
    @Operation(summary = "Actualizar producto", description = "Actualiza un producto existente")
    public ResponseEntity<ResponseProduct> update(
            @PathVariable("id") String id,
            @RequestBody RequestProductUpdate request) {
        ResponseProduct response = new ResponseProduct();

        String result = this.productBusiness.update(id, request.getDto().getProduct(), request.getDto().getVariant());

        if (result.equals("Producto actualizado correctamente")) {
            response.success();
            response.listMessage.add(result);
            DtoProduct updatedProduct = this.productBusiness.findById(id);
            response.setProduct(updatedProduct);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.error();
            response.listMessage.add(result);
            response.setProduct(request.getDto().getProduct());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping(path = "/delete/{id}")
    @Operation(summary = "Eliminar producto", description = "Elimina un producto del sistema")
    public ResponseEntity<ResponseProduct> delete(@PathVariable("id") String id) {
        ResponseProduct response = new ResponseProduct();

        String result = this.productBusiness.delete(id);

        if (result.equals("Producto eliminado correctamente")) {
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
    @Operation(summary = "Buscar productos", description = "Busca productos por nombre")
    public ResponseEntity<ResponseProductList> search(@RequestParam(required = false) String name) {
        ResponseProductList response = new ResponseProductList();

        List<DtoProduct> products = this.productBusiness.search(name);

        response.success();
        response.setProducts(products);
        response.listMessage.add("Búsqueda completada");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(path = "/category/{idCategory}")
    @Operation(summary = "Productos por categoría", description = "Obtiene todos los productos de una categoría")
    public ResponseEntity<ResponseProductList> getByCategory(@PathVariable("idCategory") String idCategory) {
        ResponseProductList response = new ResponseProductList();

        List<DtoProduct> products = this.productBusiness.findByCategory(idCategory);

        response.success();
        response.setProducts(products);
        response.listMessage.add("Productos obtenidos correctamente");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(path = "/brand/{idBrand}")
    @Operation(summary = "Productos por marca", description = "Obtiene todos los productos de una marca")
    public ResponseEntity<ResponseProductList> getByBrand(@PathVariable("idBrand") String idBrand) {
        ResponseProductList response = new ResponseProductList();

        List<DtoProduct> products = this.productBusiness.findByBrand(idBrand);

        response.success();
        response.setProducts(products);
        response.listMessage.add("Productos obtenidos correctamente");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(path = "/status/{status}")
    @Operation(summary = "Productos por estado", description = "Obtiene todos los productos con un estado específico")
    public ResponseEntity<ResponseProductList> getByStatus(@PathVariable("status") String status) {
        ResponseProductList response = new ResponseProductList();

        List<DtoProduct> products = this.productBusiness.findByStatus(status);

        response.success();
        response.setProducts(products);
        response.listMessage.add("Productos obtenidos correctamente");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
