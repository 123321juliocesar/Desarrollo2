package com.epiis.app.business;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.epiis.app.controller.reqresp.order.DtoOrderItemResponse;
import com.epiis.app.controller.reqresp.order.DtoOrderResponse;
import com.epiis.app.entity.Cart;
import com.epiis.app.entity.CartItem;
import com.epiis.app.entity.Order;
import com.epiis.app.entity.OrderItem;
import com.epiis.app.entity.User;
import com.epiis.app.repository.CartItemRepository;
import com.epiis.app.repository.CartRepository;
import com.epiis.app.repository.OrderItemRepository;
import com.epiis.app.repository.OrderRepository;
import com.epiis.app.repository.UserRepository;

import java.util.Map;
import java.util.HashMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Service
public class OrderBusiness {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private PricingBusiness pricingBusiness;

    /**
     * Crea una orden desde el carrito del usuario
     * 
     * @param idUser             - ID del usuario
     * @param shippingAddress    - Dirección de envío
     * @param shippingCity       - Ciudad de envío
     * @param shippingPostalCode - Código postal
     * @param shippingProvince   - Provincia
     * @param shippingCountry    - País
     * @return DtoOrderResponse con la orden creada o mensaje de error
     */
    @Transactional
    public DtoOrderResponse createOrderFromCart(
            String idUser,
            String shippingAddress,
            String shippingCity,
            String shippingPostalCode,
            String shippingProvince,
            String shippingCountry) {

        // Validación: Usuario existe
        Optional<User> userOptional = userRepository.findById(idUser);
        if (!userOptional.isPresent()) {
            throw new RuntimeException("Usuario no encontrado");
        }

        User user = userOptional.get();

        // Validación: Carrito existe y no está vacío
        Optional<Cart> cartOptional = cartRepository.findByUser_IdUser(idUser);
        if (!cartOptional.isPresent()) {
            throw new RuntimeException("El usuario no tiene un carrito");
        }

        Cart cart = cartOptional.get();
        List<CartItem> cartItems = cartItemRepository.findByCart_IdCart(cart.getIdCart());

        if (cartItems.isEmpty()) {
            throw new RuntimeException("El carrito está vacío");
        }

        // Validación: Todos los items tienen stock suficiente
        for (CartItem item : cartItems) {
            if (item.getVariant().getStock() < item.getQuantity()) {
                throw new RuntimeException("Stock insuficiente para el producto: " + item.getProduct().getName()
                        + " (Variante: " + item.getVariant().getSize() + " - " + item.getVariant().getColor() + ")");
            }
        }

        // Validación: Datos de envío completos
        if (shippingAddress == null || shippingAddress.trim().isEmpty()) {
            throw new RuntimeException("La dirección de envío es obligatoria");
        }
        if (shippingCity == null || shippingCity.trim().isEmpty()) {
            throw new RuntimeException("La ciudad de envío es obligatoria");
        }
        if (shippingPostalCode == null || shippingPostalCode.trim().isEmpty()) {
            throw new RuntimeException("El código postal es obligatorio");
        }
        if (shippingProvince == null || shippingProvince.trim().isEmpty()) {
            throw new RuntimeException("La provincia es obligatoria");
        }
        if (shippingCountry == null || shippingCountry.trim().isEmpty()) {
            throw new RuntimeException("El país es obligatorio");
        }

        // Calcular subtotal
        BigDecimal subtotal = cartItems.stream()
                .map(item -> item.getUnitPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calcular costo de envío
        BigDecimal shippingCost = pricingBusiness.calculateShippingCost(subtotal);

        // Calcular total
        BigDecimal totalAmount = pricingBusiness.calculateTotal(subtotal, shippingCost);

        // Crear orden
        Order order = new Order();
        order.setIdOrder(UUID.randomUUID().toString());
        order.setUser(user);
        order.setOrderNumber(generateOrderNumber());
        order.setOrderDate(new Timestamp(new Date().getTime()));
        order.setTotalAmount(totalAmount);
        order.setSubtotal(subtotal);
        order.setShippingCost(shippingCost);
        order.setStatus("pending");
        order.setShippingAddress(shippingAddress);
        order.setShippingCity(shippingCity);
        order.setShippingPostalCode(shippingPostalCode);
        order.setShippingProvince(shippingProvince);
        order.setShippingCountry(shippingCountry);

        Date now = new Date();
        order.setCreatedAt(new Timestamp(now.getTime()));
        order.setUpdatedAt(new Timestamp(now.getTime()));

        // Guardar orden
        order = orderRepository.save(order);

        // Crear OrderItems desde CartItems
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setIdOrderItem(UUID.randomUUID().toString());
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setVariant(cartItem.getVariant());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setUnitPrice(cartItem.getUnitPrice());
            orderItem.setCreatedAt(new Timestamp(now.getTime()));
            orderItem.setUpdatedAt(new Timestamp(now.getTime()));

            orderItemRepository.save(orderItem);
        }

        // Vaciar carrito
        //No vaciamos el carrito aquí (Ghost Cart Strategy).
        //cartItemRepository.deleteByCart_IdCart(cart.getIdCart());
        cart.setUpdatedAt(new Timestamp(new Date().getTime()));
        cartRepository.save(cart);

        // Retornar respuesta
        return getOrderById(order.getIdOrder());
    }

    /**
     * Obtiene una orden por su ID
     * 
     * @param idOrder - ID de la orden
     * @return DtoOrderResponse con los detalles de la orden
     */

    public DtoOrderResponse getOrderById(String idOrder) {
        Optional<Order> orderOptional = orderRepository.findByIdOrder(idOrder);
        
        if (!orderOptional.isPresent()) {
            throw new RuntimeException("Orden no encontrada");
        }

        return convertToOrderResponse(orderOptional.get());
    }

    /**
     * Obtiene todas las órdenes de un usuario
     * 
     * @param idUser - ID del usuario
     * @return Lista de DtoOrderResponse
     */
    @Transactional(readOnly = true)
    public List<DtoOrderResponse> getUserOrders(String idUser) {
        //List<Order> orders = orderRepository.findByUser_IdUser(idUser);
        // EntityGraph ya carga todo lo necesario en una sola consulta
        List<Order> orders = orderRepository.findByUser_IdUserOrderByOrderDateDesc(idUser);
        return orders.stream()
                .map(this::convertToOrderResponse)
                .collect(Collectors.toList());
    }

     /**
     * Obtiene todas las órdenes (para admin)
     * 
     * @return Lista de todas las órdenes
     */
    @Transactional(readOnly = true)
     public List<DtoOrderResponse> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .map(this::convertToOrderResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todas las órdenes paginadas (para admin), con filtro opcional de
     * estado
     * 
     * @param page   - Número de página (0-based)
     * @param size   - Tamaño de página
     * @param status - Estado para filtrar (opcional, "all" para todos)
     * @return Map con contenido y metadatos de paginación
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getAllOrdersPaginated(int page, int size, String status) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "orderDate"));
        Page<Order> orderPage;

        if (status != null && !status.equals("all") && !status.isEmpty()) {
            orderPage = orderRepository.findByStatus(status, pageable);
        } else {
            //orderPage = orderRepository.findAll(pageable);
            // Default view: Exclude 'pending' status so Admin only sees actionable orders.
            // This prevents "empty pages" caused by pages full of hidden pending orders.
            orderPage = orderRepository.findByStatusNot("pending", pageable);
        }

        List<DtoOrderResponse> content = orderPage.getContent().stream()
                .map(this::convertToOrderResponse)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("content", content);
        response.put("currentPage", orderPage.getNumber());
        response.put("totalItems", orderPage.getTotalElements());
        response.put("totalPages", orderPage.getTotalPages());
        response.put("size", orderPage.getSize());

        return response;
    }

    /**
     * Elimina una orden (solo si está cancelada)
     * 
     * @param idOrder - ID de la orden
     */
    @Transactional
    public void deleteOrder(String idOrder) {
        Order order = orderRepository.findByIdOrder(idOrder)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));

        if (!"cancelled".equalsIgnoreCase(order.getStatus())) {
            throw new RuntimeException("Solo se pueden eliminar órdenes con estado 'cancelado'");
        }

        orderRepository.delete(order);
    }

    /**
     * Obtiene órdenes por estado (para admin)
     * 
     * @param status - Estado a filtrar
     * @return Lista de órdenes con ese estado
     */
    @Transactional(readOnly = true)
    public List<DtoOrderResponse> getOrdersByStatus(String status) {
        List<Order> orders = orderRepository.findByStatus(status);
        return orders.stream()
                .map(this::convertToOrderResponse)
                .collect(Collectors.toList());
    }

    /**
     * Actualiza el estado de una orden
     * 
     * @param idOrder   - ID de la orden
     * @param newStatus - Nuevo estado
     * @return DtoOrderResponse actualizado
     */
    @Transactional
    public DtoOrderResponse updateOrderStatus(String idOrder, String newStatus) {
        Optional<Order> orderOptional = orderRepository.findByIdOrder(idOrder);
        
        if (!orderOptional.isPresent()) {
            throw new RuntimeException("Orden no encontrada");
        }

        Order order = orderOptional.get();

        // Validar estados válidos
        List<String> validStatuses = List.of("pending", "confirmed", "shipped", "delivered", "cancelled");
        if (!validStatuses.contains(newStatus)) {
            throw new RuntimeException("Estado inválido. Estados válidos: " + String.join(", ", validStatuses));
        }

        order.setStatus(newStatus);
        order.setUpdatedAt(new Timestamp(new Date().getTime()));
        orderRepository.save(order);

        return convertToOrderResponse(order);
    }

    /**
     * Genera un número de orden único
     * Formato: ORD-YYYYMMDD-XXXX
     * 
     * @return Número de orden generado
     */
    private String generateOrderNumber() {
        LocalDateTime now = LocalDateTime.now();
        String datePart = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        // Generar 4 dígitos aleatorios
        int randomPart = (int) (Math.random() * 10000);
        String orderNumber = String.format("ORD-%s-%04d", datePart, randomPart);

        // Verificar que no exista (muy improbable, pero por seguridad)
        while (orderRepository.findByOrderNumber(orderNumber).isPresent()) {
            randomPart = (int) (Math.random() * 10000);
            orderNumber = String.format("ORD-%s-%04d", datePart, randomPart);
        }

        return orderNumber;
    }

    /**
     * Convierte una entidad Order a DtoOrderResponse
     */
    private DtoOrderResponse convertToOrderResponse(Order order) {
        DtoOrderResponse dto = new DtoOrderResponse();

        dto.setIdOrder(order.getIdOrder());
        dto.setIdUser(order.getUser().getIdUser());
        dto.setUserName(order.getUser().getFirstName() + " " + order.getUser().getLastName());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setOrderDate(order.getOrderDate());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setSubtotal(order.getSubtotal());
        dto.setShippingCost(order.getShippingCost());
        dto.setStatus(order.getStatus());
        dto.setShippingAddress(order.getShippingAddress());
        dto.setShippingCity(order.getShippingCity());
        dto.setShippingPostalCode(order.getShippingPostalCode());
        dto.setShippingProvince(order.getShippingProvince());
        dto.setShippingCountry(order.getShippingCountry());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());

        // Usar los items ya cargados por EntityGraph en lugar de consultar nuevamente
        List<OrderItem> orderItems = order.getOrderItems();
        if (orderItems != null) {
            List<DtoOrderItemResponse> itemDtos = orderItems.stream()
                    .map(this::convertToOrderItemResponse)
                    .collect(Collectors.toList());

            dto.setItems(itemDtos);
            dto.setTotalItems(orderItems.stream().mapToInt(OrderItem::getQuantity).sum());
        } else {
            dto.setItems(List.of());
            dto.setTotalItems(0);
        }

        return dto;
    }

    /**
     * Convierte una entidad OrderItem a DtoOrderItemResponse
     */
    private DtoOrderItemResponse convertToOrderItemResponse(OrderItem item) {
        DtoOrderItemResponse dto = new DtoOrderItemResponse();

        dto.setIdOrderItem(item.getIdOrderItem());
        dto.setIdOrder(item.getOrder().getIdOrder());
        dto.setIdProduct(item.getProduct().getIdProduct());
        dto.setIdVariant(item.getVariant() != null ? item.getVariant().getIdVariant() : null);
        dto.setQuantity(item.getQuantity());
        dto.setUnitPrice(item.getUnitPrice());

        // Detalles del producto
        dto.setProductName(item.getProduct().getName());
        dto.setProductImageUrl(item.getProduct().getImageUrl());

        // Detalles de la variante
        if (item.getVariant() != null) {
            dto.setVariantSize(item.getVariant().getSize());
            dto.setVariantColor(item.getVariant().getColor());
            dto.setVariantColorHex(item.getVariant().getColorHex());
        }

        return dto;
    }
}
