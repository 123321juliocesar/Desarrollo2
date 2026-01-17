package com.epiis.app.business;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.epiis.app.config.PayPalService;
import com.epiis.app.controller.reqresp.payment.DtoCaptureResponse;
import com.epiis.app.controller.reqresp.payment.DtoPaymentResponse;
import com.epiis.app.entity.Order;
import com.epiis.app.entity.OrderItem;
import com.epiis.app.entity.Payment;
import com.epiis.app.entity.ProductVariant;
import com.epiis.app.repository.OrderItemRepository;
import com.epiis.app.repository.OrderRepository;
import com.epiis.app.repository.PaymentRepository;
import com.epiis.app.repository.ProductVariantRepository;
import com.paypal.http.HttpResponse;
import com.paypal.orders.AmountBreakdown;
import com.paypal.orders.AmountWithBreakdown;
import com.paypal.orders.ApplicationContext;
import com.paypal.orders.LinkDescription;
import com.paypal.orders.Money;
import com.paypal.orders.OrderRequest;
import com.paypal.orders.OrdersCaptureRequest;
import com.paypal.orders.OrdersCreateRequest;
import com.paypal.orders.PurchaseUnitRequest;

@Service
public class PaymentBusiness {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PayPalService payPalService;

    @Autowired
    private CurrencyBusiness currencyBusiness;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    /**
     * Crea una orden de pago en PayPal
     * 
     * @param idOrder   - ID de la orden
     * @param returnUrl - URL de retorno (opcional)
     * @param cancelUrl - URL de cancelación (opcional)
     * @return DtoPaymentResponse con approvalUrl
     */
    @Transactional
    public DtoPaymentResponse createPayPalOrder(String idOrder, String returnUrl, String cancelUrl) {
        // Validación: Orden existe
        Optional<Order> orderOptional = orderRepository.findById(idOrder);
        if (!orderOptional.isPresent()) {
            throw new RuntimeException("Orden no encontrada");
        }

        Order order = orderOptional.get();

        // Validación: Orden en estado pending
        if (!"pending".equals(order.getStatus())) {
            throw new RuntimeException("La orden debe estar en estado 'pending' para crear un pago");
        }

        // Validación: No existe pago previo (idempotencia)
        Optional<Payment> existingPayment = paymentRepository.findByOrder_IdOrder(idOrder);
        if (existingPayment.isPresent()) {
            throw new RuntimeException("Ya existe un pago para esta orden");
        }

        try {
            // Crear request de orden en PayPal
            OrdersCreateRequest request = new OrdersCreateRequest();
            request.prefer("return=representation");

            // Configurar URLs de retorno
            String finalReturnUrl = returnUrl != null ? returnUrl : "https://sumaqsport.netlify.app/payment/success";
            String finalCancelUrl = cancelUrl != null ? cancelUrl : "https://sumaqsport.netlify.app/payment/cancel";

            // Convertir montos a USD usando CurrencyBusiness
            BigDecimal totalUsd = currencyBusiness.convertPenToUsd(order.getTotalAmount());
            BigDecimal subtotalUsd = currencyBusiness.convertPenToUsd(order.getSubtotal());
            BigDecimal shippingUsd = currencyBusiness.convertPenToUsd(order.getShippingCost());

            // Ajuste por redondeo: asegurar que la suma de items + envío sea igual al total
            BigDecimal calculatedTotal = subtotalUsd.add(shippingUsd);
            if (calculatedTotal.compareTo(totalUsd) != 0) {
                // Si hay diferencia por redondeo, ajustar el subtotal
                subtotalUsd = totalUsd.subtract(shippingUsd);
            }

            // Construir orden de PayPal
            OrderRequest orderRequest = new OrderRequest();
            orderRequest.checkoutPaymentIntent("CAPTURE");

            // Configurar contexto de aplicación
            ApplicationContext applicationContext = new ApplicationContext()
                    .returnUrl(finalReturnUrl)
                    .cancelUrl(finalCancelUrl)
                    .brandName("Sumac Sport")
                    .landingPage("BILLING")
                    .userAction("PAY_NOW");
            orderRequest.applicationContext(applicationContext);

            // Configurar unidad de compra
            PurchaseUnitRequest purchaseUnitRequest = new PurchaseUnitRequest()
                    .referenceId(order.getOrderNumber())
                    .description("Orden #" + order.getOrderNumber())
                    .amountWithBreakdown(new AmountWithBreakdown()
                            .currencyCode("USD")
                            .value(totalUsd.toString())
                            .amountBreakdown(new AmountBreakdown()
                                    .itemTotal(new Money().currencyCode("USD").value(subtotalUsd.toString()))
                                    .shipping(new Money().currencyCode("USD").value(shippingUsd.toString()))));

            List<PurchaseUnitRequest> purchaseUnits = new ArrayList<>();
            purchaseUnits.add(purchaseUnitRequest);
            orderRequest.purchaseUnits(purchaseUnits);

            request.requestBody(orderRequest);

            // Ejecutar request en PayPal
            HttpResponse<com.paypal.orders.Order> response = payPalService.getPayPalHttpClient().execute(request);
            com.paypal.orders.Order paypalOrder = response.result();

            // Obtener approval URL
            String approvalUrl = null;
            for (LinkDescription link : paypalOrder.links()) {
                if ("approve".equals(link.rel())) {
                    approvalUrl = link.href();
                    break;
                }
            }

            // Guardar pago en BD
            Payment payment = new Payment();
            payment.setIdPayment(UUID.randomUUID().toString());
            payment.setOrder(order);
            payment.setPaymentMethod("paypal");
            payment.setStatus("pending");
            payment.setTransactionId(paypalOrder.id());
            payment.setAmount(order.getTotalAmount());
            payment.setPaymentDate(new Timestamp(new Date().getTime()));

            Date now = new Date();
            payment.setCreatedAt(new Timestamp(now.getTime()));
            payment.setUpdatedAt(new Timestamp(now.getTime()));

            payment = paymentRepository.save(payment);

            // Construir respuesta
            DtoPaymentResponse dto = new DtoPaymentResponse();
            dto.setIdPayment(payment.getIdPayment());
            dto.setIdOrder(payment.getOrder().getIdOrder());
            dto.setPaymentMethod(payment.getPaymentMethod());
            dto.setStatus(payment.getStatus());
            dto.setTransactionId(payment.getTransactionId());
            dto.setPaymentDate(payment.getPaymentDate());
            dto.setAmount(payment.getAmount());
            dto.setCreatedAt(payment.getCreatedAt());
            dto.setUpdatedAt(payment.getUpdatedAt());
            dto.setPaypalOrderId(paypalOrder.id());
            dto.setApprovalUrl(approvalUrl);

            return dto;

        } catch (Exception e) {
            throw new RuntimeException("Error al crear orden en PayPal: " + e.getMessage(), e);
        }
    }

    /**
     * Captura un pago aprobado en PayPal
     * 
     * @param paypalOrderId - ID de la orden de PayPal
     * @return DtoCaptureResponse con detalles de la captura
     */
    @Transactional
    public DtoCaptureResponse capturePayPalOrder(String paypalOrderId) {

        // Validación: Pago existe
        Optional<Payment> paymentOptional = paymentRepository.findByTransactionId(paypalOrderId);
        if (!paymentOptional.isPresent()) {
            throw new RuntimeException("Pago no encontrado");
        }

        Payment payment = paymentOptional.get();

        // Validación: Estado es pending
        if (!"pending".equals(payment.getStatus())) {
            throw new RuntimeException("El pago debe estar en estado 'pending' para ser capturado");
        }

        try {
            // Capturar orden en PayPal
            OrdersCaptureRequest request = new OrdersCaptureRequest(paypalOrderId);
            request.prefer("return=representation");

            HttpResponse<com.paypal.orders.Order> response = payPalService.getPayPalHttpClient().execute(request);
            com.paypal.orders.Order capturedOrder = response.result();

            // Obtener capture ID
            String captureId = null;
            if (capturedOrder.purchaseUnits() != null && !capturedOrder.purchaseUnits().isEmpty()) {
                if (capturedOrder.purchaseUnits().get(0).payments() != null
                        && capturedOrder.purchaseUnits().get(0).payments().captures() != null
                        && !capturedOrder.purchaseUnits().get(0).payments().captures().isEmpty()) {
                    captureId = capturedOrder.purchaseUnits().get(0).payments().captures().get(0).id();
                }
            }

            // Actualizar pago en BD
            payment.setStatus("completed");
            payment.setPaymentDate(new Timestamp(new Date().getTime()));
            payment.setUpdatedAt(new Timestamp(new Date().getTime()));
            paymentRepository.save(payment);

            // Actualizar orden a confirmed
            Order order = payment.getOrder();
            order.setStatus("confirmed");
            order.setUpdatedAt(new Timestamp(new Date().getTime()));
            orderRepository.save(order);



            // ACTUALIZACIÓN DE STOCK
            // Obtener items de la orden y reducir stock
            List<OrderItem> orderItems = orderItemRepository.findByOrder_IdOrder(order.getIdOrder());
            for (OrderItem item : orderItems) {
                ProductVariant variant = item.getVariant();
                if (variant != null) {
                    int newStock = variant.getStock() - item.getQuantity();

                    // Asegurar que el stock no sea negativo (por seguridad)
                    if (newStock < 0) {
                        newStock = 0;
                    }

                    variant.setStock(newStock);
                    variant.setUpdatedAt(new Timestamp(new Date().getTime()));
                    productVariantRepository.save(variant);
                }
            }

            // Construir respuesta
            DtoCaptureResponse dto = new DtoCaptureResponse();
            dto.setIdPayment(payment.getIdPayment());
            dto.setIdOrder(payment.getOrder().getIdOrder());
            dto.setPaymentMethod(payment.getPaymentMethod());
            dto.setStatus(payment.getStatus());
            dto.setTransactionId(payment.getTransactionId());
            dto.setPaymentDate(payment.getPaymentDate());
            dto.setAmount(payment.getAmount());
            dto.setCreatedAt(payment.getCreatedAt());
            dto.setUpdatedAt(payment.getUpdatedAt());
            dto.setPaypalOrderId(paypalOrderId);
            dto.setCaptureId(captureId);

            return dto;

        } catch (Exception e) {
            // Marcar pago como failed
            payment.setStatus("failed");
            payment.setUpdatedAt(new Timestamp(new Date().getTime()));
            paymentRepository.save(payment);

            throw new RuntimeException("Error al capturar pago en PayPal: " + e.getMessage(), e);
        }
    }

    /**
     * Obtiene el pago de una orden
     * 
     * @param idOrder - ID de la orden
     * @return DtoPaymentResponse
     */
    public DtoPaymentResponse getPaymentByOrder(String idOrder) {
        Optional<Payment> paymentOptional = paymentRepository.findByOrder_IdOrder(idOrder);
        if (!paymentOptional.isPresent()) {
            throw new RuntimeException("No se encontró pago para esta orden");
        }

        Payment payment = paymentOptional.get();

        DtoPaymentResponse dto = new DtoPaymentResponse();
        dto.setIdPayment(payment.getIdPayment());
        dto.setIdOrder(payment.getOrder().getIdOrder());
        dto.setPaymentMethod(payment.getPaymentMethod());
        dto.setStatus(payment.getStatus());
        dto.setTransactionId(payment.getTransactionId());
        dto.setPaymentDate(payment.getPaymentDate());
        dto.setAmount(payment.getAmount());
        dto.setCreatedAt(payment.getCreatedAt());
        dto.setUpdatedAt(payment.getUpdatedAt());
        dto.setPaypalOrderId(payment.getTransactionId());

        return dto;
    }
}
