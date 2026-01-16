package com.epiis.app.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.epiis.app.business.OrderBusiness;
import com.epiis.app.controller.reqresp.order.CreateOrderRequest;
import com.epiis.app.controller.reqresp.order.DtoOrderResponse;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    @Autowired
    private OrderBusiness orderBusiness;

    /**
     * Crea una orden desde el carrito del usuario
     * POST /api/orders/create
     * 
     * @param request - Datos de envío
     * @return DtoOrderResponse con la orden creada
     */
    @PostMapping("/create")
    public ResponseEntity<?> createOrder(@RequestBody CreateOrderRequest request) {
        try {
            DtoOrderResponse response = orderBusiness.createOrderFromCart(
                    request.getIdUser(),
                    request.getShippingAddress(),
                    request.getShippingCity(),
                    request.getShippingPostalCode(),
                    request.getShippingProvince(),
                    request.getShippingCountry());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al crear la orden: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Obtiene una orden específica por su ID
     * GET /api/orders/{idOrder}
     * 
     * @param idOrder - ID de la orden
     * @return DtoOrderResponse con los detalles de la orden
     */
    @GetMapping("/{idOrder}")
    public ResponseEntity<?> getOrder(@PathVariable String idOrder) {
        try {
            DtoOrderResponse response = orderBusiness.getOrderById(idOrder);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al obtener la orden: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    /**
     * Obtiene todas las órdenes de un usuario
     * GET /api/orders/user/{idUser}
     * 
     * @param idUser - ID del usuario
     * @return Lista de DtoOrderResponse
     */
    @GetMapping("/user/{idUser}")
    public ResponseEntity<?> getUserOrders(@PathVariable String idUser) {
        try {
            List<DtoOrderResponse> response = orderBusiness.getUserOrders(idUser);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al obtener las órdenes: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Actualiza el estado de una orden
     * PUT /api/orders/{idOrder}/status
     * 
     * @param idOrder   - ID de la orden
     * @param newStatus - Nuevo estado (query param)
     * @return DtoOrderResponse actualizado
     */
    @PutMapping("/{idOrder}/status")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable String idOrder,
            @RequestParam String newStatus) {
        try {
            DtoOrderResponse response = orderBusiness.updateOrderStatus(idOrder, newStatus);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al actualizar el estado: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    
    /**
     * Obtiene todas las órdenes (solo admin)
     * GET /api/orders/all
     */
    @GetMapping("/all")
    public ResponseEntity<?> getAllOrders() {
        try {
            List<DtoOrderResponse> response = orderBusiness.getAllOrders();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al obtener las órdenes: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Obtiene órdenes por estado
     * GET /api/orders/status/{status}
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<?> getOrdersByStatus(@PathVariable String status) {
        try {
            List<DtoOrderResponse> response = orderBusiness.getOrdersByStatus(status);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al obtener las órdenes: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
}
