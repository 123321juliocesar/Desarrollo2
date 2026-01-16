package com.epiis.app.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.epiis.app.business.CartBusiness;
import com.epiis.app.controller.reqresp.cart.AddToCartRequest;
import com.epiis.app.controller.reqresp.cart.UpdateCartItemRequest;
import com.epiis.app.controller.reqresp.cart.DtoCartResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "*")
public class CartController {

    @Autowired
    private CartBusiness cartBusiness;

    /**
     * Agrega un producto al carrito
     * POST /api/cart/add
     * 
     * @param request - Datos del producto a agregar (idUser, idProduct, idVariant,
     *                quantity)
     * @return DtoCartResponse con el carrito actualizado
     */
    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@Valid @RequestBody AddToCartRequest request) {
        try {
            DtoCartResponse response = cartBusiness.addToCart(
                    request.getIdUser(),
                    request.getIdProduct(),
                    request.getIdVariant(),
                    request.getQuantity());

            // Verificar si hubo error
            if ("ERROR".equals(response.getIdCart())) {
                Map<String, String> error = new HashMap<>();
                error.put("error", response.getIdUser()); // El mensaje de error está en idUser
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al agregar producto al carrito: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Obtiene el carrito de un usuario
     * GET /api/cart/{idUser}
     * 
     * @param idUser - ID del usuario
     * @return DtoCartResponse con el carrito y sus items
     */
    @GetMapping("/{idUser}")
    public ResponseEntity<?> getCart(@PathVariable String idUser) {
        try {
            DtoCartResponse response = cartBusiness.getCartByUser(idUser);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al obtener el carrito: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Actualiza la cantidad de un item del carrito
     * PUT /api/cart/item/{idCartItem}
     * 
     * @param idCartItem - ID del item a actualizar
     * @param request    - Datos de actualización (idUser, quantity)
     * @return DtoCartResponse con el carrito actualizado
     */
    @PutMapping("/item/{idCartItem}")
    public ResponseEntity<?> updateCartItem(
            @PathVariable String idCartItem,
            @Valid @RequestBody UpdateCartItemRequest request) {
        try {
            DtoCartResponse response = cartBusiness.updateCartItemQuantity(
                    request.getIdUser(),
                    idCartItem,
                    request.getQuantity());

            // Verificar si hubo error
            if ("ERROR".equals(response.getIdCart())) {
                Map<String, String> error = new HashMap<>();
                error.put("error", response.getIdUser()); // El mensaje de error está en idUser
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al actualizar item del carrito: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Elimina un item del carrito
     * DELETE /api/cart/item/{idCartItem}
     * 
     * @param idCartItem - ID del item a eliminar
     * @param idUser     - ID del usuario (query param)
     * @return Mensaje de confirmación
     */
    @DeleteMapping("/item/{idCartItem}")
    public ResponseEntity<?> removeCartItem(
            @PathVariable String idCartItem,
            @RequestParam String idUser) {
        try {
            String message = cartBusiness.removeCartItem(idUser, idCartItem);

            Map<String, String> response = new HashMap<>();
            response.put("message", message);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al eliminar item del carrito: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Vacía el carrito del usuario
     * DELETE /api/cart/clear/{idUser}
     * 
     * @param idUser - ID del usuario
     * @return Mensaje de confirmación
     */
    @DeleteMapping("/clear/{idUser}")
    public ResponseEntity<?> clearCart(@PathVariable String idUser) {
        try {
            String message = cartBusiness.clearCart(idUser);

            Map<String, String> response = new HashMap<>();
            response.put("message", message);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al vaciar el carrito: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
