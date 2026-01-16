package com.epiis.app.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epiis.app.business.PaymentBusiness;
import com.epiis.app.controller.reqresp.payment.CreatePaymentRequest;
import com.epiis.app.controller.reqresp.payment.DtoCaptureResponse;
import com.epiis.app.controller.reqresp.payment.DtoPaymentResponse;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    @Autowired
    private PaymentBusiness paymentBusiness;

    /**
     * Crea una orden de pago en PayPal
     * POST /api/payments/create
     * 
     * @param request - Datos de la orden y URLs de retorno
     * @return DtoPaymentResponse con approvalUrl
     */
    @PostMapping("/create")
    public ResponseEntity<?> createPayment(@RequestBody CreatePaymentRequest request) {
        try {
            DtoPaymentResponse response = paymentBusiness.createPayPalOrder(
                    request.getIdOrder(),
                    request.getReturnUrl(),
                    request.getCancelUrl());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al crear el pago: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Captura un pago aprobado en PayPal
     * POST /api/payments/capture/{paypalOrderId}
     * 
     * @param paypalOrderId - ID de la orden de PayPal
     * @return DtoCaptureResponse con detalles de la captura
     */
    @PostMapping("/capture/{paypalOrderId}")
    public ResponseEntity<?> capturePayment(@PathVariable String paypalOrderId) {
        try {
            DtoCaptureResponse response = paymentBusiness.capturePayPalOrder(paypalOrderId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al capturar el pago: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Obtiene el pago de una orden
     * GET /api/payments/order/{idOrder}
     * 
     * @param idOrder - ID de la orden
     * @return DtoPaymentResponse con información del pago
     */
    @GetMapping("/order/{idOrder}")
    public ResponseEntity<?> getPaymentByOrder(@PathVariable String idOrder) {
        try {
            DtoPaymentResponse response = paymentBusiness.getPaymentByOrder(idOrder);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al obtener el pago: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }
}
