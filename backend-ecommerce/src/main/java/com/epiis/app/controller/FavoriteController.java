package com.epiis.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.epiis.app.business.FavoriteBusiness;
import com.epiis.app.controller.reqresp.favorite.RequestFavoriteToggle;
import com.epiis.app.controller.reqresp.favorite.ResponseFavorite;
import com.epiis.app.controller.reqresp.favorite.ResponseFavoriteCheck;
import com.epiis.app.controller.reqresp.favorite.ResponseFavoriteList;
import com.epiis.app.dto.DtoFavoriteToggleResult;
import com.epiis.app.dto.DtoFavoriteWithProduct;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(path = "/favorite")
@Tag(name = "Favorite", description = "API de gestión de favoritos (lista de deseos)")
public class FavoriteController {

    @Autowired
    private FavoriteBusiness favoriteBusiness;

    @PostMapping(path = "/toggle")
    @Operation(summary = "Toggle favorito", description = "Agrega o elimina un producto de favoritos en un solo endpoint")
    public ResponseEntity<ResponseFavorite> toggle(
            @RequestParam String idUser,
            @RequestBody RequestFavoriteToggle request) {
        ResponseFavorite response = new ResponseFavorite();

        String idProduct = request.getDto().getFavorite().getIdProduct();
        DtoFavoriteToggleResult result = this.favoriteBusiness.toggleFavorite(idUser, idProduct);

        if (result.getError() != null) {
            response.error();
            response.listMessage.add(result.getError());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        response.success();
        response.setAction(result.getAction());
        response.listMessage.add(result.getMessage());

        if ("added".equals(result.getAction())) {
            response.setFavorite(result.getFavorite());
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    @GetMapping
    @Operation(summary = "Listar favoritos", description = "Obtiene todos los favoritos de un usuario con información del producto")
    public ResponseEntity<ResponseFavoriteList> list(@RequestParam String idUser) {
        ResponseFavoriteList response = new ResponseFavoriteList();

        List<DtoFavoriteWithProduct> favorites = this.favoriteBusiness.getFavoritesByUser(idUser);

        response.success();
        response.setFavorites(favorites);
        response.listMessage.add("Favoritos obtenidos correctamente");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(path = "/check/{idProduct}")
    @Operation(summary = "Verificar favorito", description = "Verifica si un producto está en la lista de favoritos del usuario")
    public ResponseEntity<ResponseFavoriteCheck> check(
            @PathVariable("idProduct") String idProduct,
            @RequestParam String idUser) {
        ResponseFavoriteCheck response = new ResponseFavoriteCheck();

        boolean isFavorite = this.favoriteBusiness.checkFavorite(idUser, idProduct);

        response.success();
        response.setFavorite(isFavorite);
        response.listMessage.add("Verificación completada");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping(path = "/{idProduct}")
    @Operation(summary = "Eliminar favorito", description = "Elimina un producto de la lista de favoritos")
    public ResponseEntity<ResponseFavorite> delete(
            @PathVariable("idProduct") String idProduct,
            @RequestParam String idUser) {
        ResponseFavorite response = new ResponseFavorite();

        String result = this.favoriteBusiness.removeFavorite(idUser, idProduct);

        if (result.equals("Producto eliminado de favoritos")) {
            response.success();
            response.setAction("removed");
            response.listMessage.add(result);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.error();
            response.listMessage.add(result);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }
}
