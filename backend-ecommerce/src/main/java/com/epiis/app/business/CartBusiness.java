package com.epiis.app.business;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.epiis.app.controller.reqresp.cart.DtoCartItemWithDetails;
import com.epiis.app.controller.reqresp.cart.DtoCartResponse;
import com.epiis.app.dto.DtoProduct;
import com.epiis.app.dto.DtoProductVariant;
import com.epiis.app.entity.Cart;
import com.epiis.app.entity.CartItem;
import com.epiis.app.entity.Product;
import com.epiis.app.entity.ProductVariant;
import com.epiis.app.entity.User;
import com.epiis.app.repository.CartItemRepository;
import com.epiis.app.repository.CartRepository;
import com.epiis.app.repository.ProductRepository;
import com.epiis.app.repository.ProductVariantRepository;
import com.epiis.app.repository.UserRepository;

@Service
public class CartBusiness {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    @Autowired
    private ProductBusiness productBusiness;

    @Autowired
    private ProductVariantBusiness productVariantBusiness;

    @Autowired
    private PricingBusiness pricingBusiness;

    /**
     * Agrega un producto al carrito con variante
     * 
     * @param idUser    - ID del usuario
     * @param idProduct - ID del producto
     * @param idVariant - ID de la variante (obligatorio)
     * @param quantity  - Cantidad a agregar
     * @return DtoCartResponse con el carrito actualizado o mensaje de error
     */
    @Transactional
    public DtoCartResponse addToCart(String idUser, String idProduct, String idVariant, Integer quantity) {
        DtoCartResponse response = new DtoCartResponse();

        // Validación: Usuario existe
        Optional<User> userOptional = userRepository.findById(idUser);
        if (!userOptional.isPresent()) {
            response.setIdCart("ERROR");
            response.setIdUser("Usuario no encontrado");
            return response;
        }

        // Validación: Producto existe
        Optional<Product> productOptional = productRepository.findById(idProduct);
        if (!productOptional.isPresent()) {
            response.setIdCart("ERROR");
            response.setIdUser("Producto no encontrado");
            return response;
        }

        Product product = productOptional.get();

        // Validación: Producto activo
        if (!"active".equals(product.getStatus())) {
            response.setIdCart("ERROR");
            response.setIdUser("El producto no está disponible");
            return response;
        }

        // Validación: Variante obligatoria
        if (idVariant == null || idVariant.trim().isEmpty()) {
            response.setIdCart("ERROR");
            response.setIdUser("La variante es obligatoria");
            return response;
        }

        // Validación: Variante existe
        Optional<ProductVariant> variantOptional = productVariantRepository.findById(idVariant);
        if (!variantOptional.isPresent()) {
            response.setIdCart("ERROR");
            response.setIdUser("Variante no encontrada");
            return response;
        }

        ProductVariant variant = variantOptional.get();

        // Validación: Variante pertenece al producto
        if (!variant.getProduct().getIdProduct().equals(idProduct)) {
            response.setIdCart("ERROR");
            response.setIdUser("La variante no pertenece al producto especificado");
            return response;
        }

        // Validación: Cantidad válida
        if (quantity == null || quantity < 1) {
            response.setIdCart("ERROR");
            response.setIdUser("La cantidad debe ser al menos 1");
            return response;
        }

        // Validación: Stock disponible
        if (variant.getStock() < quantity) {
            response.setIdCart("ERROR");
            response.setIdUser("Stock insuficiente. Disponible: " + variant.getStock());
            return response;
        }

        // Obtener o crear carrito del usuario
        Cart cart = getOrCreateCart(userOptional.get());

        // Buscar si ya existe el item (mismo carrito + variante)
        Optional<CartItem> existingItemOptional = cartItemRepository
                .findByCart_IdCartAndVariant_IdVariant(cart.getIdCart(), idVariant);

        if (existingItemOptional.isPresent()) {
            // Item existe: incrementar cantidad
            CartItem existingItem = existingItemOptional.get();
            int newQuantity = existingItem.getQuantity() + quantity;

            // Validar stock para la nueva cantidad
            if (variant.getStock() < newQuantity) {
                response.setIdCart("ERROR");
                response.setIdUser("Stock insuficiente. Disponible: " + variant.getStock()
                        + ", en carrito: " + existingItem.getQuantity());
                return response;
            }

            existingItem.setQuantity(newQuantity);
            existingItem.setUpdatedAt(new Timestamp(new Date().getTime()));
            cartItemRepository.save(existingItem);
        } else {
            // Item no existe: crear nuevo
            CartItem newItem = new CartItem();
            newItem.setIdCartItem(UUID.randomUUID().toString());
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setVariant(variant);
            newItem.setQuantity(quantity);
            newItem.setUnitPrice(product.getPrice()); // Guardar precio actual

            Date now = new Date();
            newItem.setCreatedAt(new Timestamp(now.getTime()));
            newItem.setUpdatedAt(new Timestamp(now.getTime()));

            cartItemRepository.save(newItem);
        }

        // Actualizar timestamp del carrito
        cart.setUpdatedAt(new Timestamp(new Date().getTime()));
        cartRepository.save(cart);

        // Retornar carrito actualizado
        return getCartByUser(idUser);
    }

    /**
     * Obtiene el carrito del usuario con todos sus items
     * 
     * @param idUser - ID del usuario
     * @return DtoCartResponse con el carrito y sus items
     */

    public DtoCartResponse getCartByUser(String idUser) {
        DtoCartResponse response = new DtoCartResponse();

        Optional<Cart> cartOptional = cartRepository.findByUser_IdUser(idUser);

        if (!cartOptional.isPresent()) {
            // Carrito vacío
            response.setIdCart(null);
            response.setIdUser(idUser);
            response.setItems(new ArrayList<>());
            response.setTotalItems(0);
            response.setSubtotal(BigDecimal.ZERO);
            response.setShippingCost(BigDecimal.ZERO);
            response.setTotal(BigDecimal.ZERO);
            return response;
        }

        Cart cart = cartOptional.get();

        // Convertir a DTO
        response.setIdCart(cart.getIdCart());
        response.setIdUser(cart.getUser().getIdUser());
        response.setCreatedAt(cart.getCreatedAt());
        response.setUpdatedAt(cart.getUpdatedAt());

        // Obtener items del carrito
        List<CartItem> items = cartItemRepository.findByCart_IdCart(cart.getIdCart());

        // Convertir items a DTOs con detalles
        List<DtoCartItemWithDetails> itemsWithDetails = items.stream()
                .map(this::convertToItemWithDetails)
                .collect(Collectors.toList());

        response.setItems(itemsWithDetails);

        // Calcular totales
        int totalItems = items.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();

        BigDecimal subtotal = items.stream()
                .map(item -> item.getUnitPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        response.setTotalItems(totalItems);
        response.setSubtotal(subtotal);

        // Calcular costo de envío usando PricingBusiness
        BigDecimal shippingCost = pricingBusiness.calculateShippingCost(subtotal);
        // Calcular total final usando PricingBusiness
        BigDecimal total = pricingBusiness.calculateTotal(subtotal, shippingCost);

        response.setShippingCost(shippingCost);
        response.setTotal(total);

        return response;
    }

    /**
     * Actualiza la cantidad de un item del carrito
     * 
     * @param idUser      - ID del usuario
     * @param idCartItem  - ID del item del carrito
     * @param newQuantity - Nueva cantidad
     * @return DtoCartResponse actualizado o mensaje de error
     */
    @Transactional
    public DtoCartResponse updateCartItemQuantity(String idUser, String idCartItem, Integer newQuantity) {
        DtoCartResponse response = new DtoCartResponse();

        // Validación: Cantidad válida
        if (newQuantity == null || newQuantity < 1) {
            response.setIdCart("ERROR");
            response.setIdUser("La cantidad debe ser al menos 1");
            return response;
        }

        // Validación: Item existe
        Optional<CartItem> itemOptional = cartItemRepository.findById(idCartItem);
        if (!itemOptional.isPresent()) {
            response.setIdCart("ERROR");
            response.setIdUser("Item no encontrado en el carrito");
            return response;
        }

        CartItem item = itemOptional.get();

        // Validación: Item pertenece al usuario
        if (!item.getCart().getUser().getIdUser().equals(idUser)) {
            response.setIdCart("ERROR");
            response.setIdUser("El item no pertenece al carrito del usuario");
            return response;
        }

        // Validación: Stock disponible
        if (item.getVariant().getStock() < newQuantity) {
            response.setIdCart("ERROR");
            response.setIdUser("Stock insuficiente. Disponible: " + item.getVariant().getStock());
            return response;
        }

        // Actualizar cantidad
        item.setQuantity(newQuantity);
        item.setUpdatedAt(new Timestamp(new Date().getTime()));
        cartItemRepository.save(item);

        // Actualizar timestamp del carrito
        Cart cart = item.getCart();
        cart.setUpdatedAt(new Timestamp(new Date().getTime()));
        cartRepository.save(cart);

        // Retornar carrito actualizado
        return getCartByUser(idUser);
    }

    /**
     * Elimina un item del carrito
     * 
     * @param idUser     - ID del usuario
     * @param idCartItem - ID del item a eliminar
     * @return Mensaje de confirmación o error
     */
    @Transactional
    public String removeCartItem(String idUser, String idCartItem) {
        // Validación: Item existe
        Optional<CartItem> itemOptional = cartItemRepository.findById(idCartItem);
        if (!itemOptional.isPresent()) {
            return "Item no encontrado en el carrito";
        }

        CartItem item = itemOptional.get();

        // Validación: Item pertenece al usuario
        if (!item.getCart().getUser().getIdUser().equals(idUser)) {
            return "El item no pertenece al carrito del usuario";
        }

        // Actualizar timestamp del carrito antes de eliminar
        Cart cart = item.getCart();
        cart.setUpdatedAt(new Timestamp(new Date().getTime()));
        cartRepository.save(cart);

        // Eliminar item
        cartItemRepository.delete(item);

        return "Item eliminado del carrito correctamente";
    }

    /**
     * Vacía el carrito del usuario
     * 
     * @param idUser - ID del usuario
     * @return Mensaje de confirmación
     */
    @Transactional
    public String clearCart(String idUser) {
        Optional<Cart> cartOptional = cartRepository.findByUser_IdUser(idUser);

        if (!cartOptional.isPresent()) {
            return "El usuario no tiene un carrito";
        }

        Cart cart = cartOptional.get();

        // Eliminar todos los items
        cartItemRepository.deleteByCart_IdCart(cart.getIdCart());

        // Actualizar timestamp del carrito
        cart.setUpdatedAt(new Timestamp(new Date().getTime()));
        cartRepository.save(cart);

        return "Carrito vaciado correctamente";
    }

    /**
     * Obtiene o crea el carrito de un usuario
     * 
     * @param user - Usuario
     * @return Cart del usuario
     */
    private Cart getOrCreateCart(User user) {
        Optional<Cart> cartOptional = cartRepository.findByUser_IdUser(user.getIdUser());

        if (cartOptional.isPresent()) {
            return cartOptional.get();
        }

        // Crear nuevo carrito
        Cart newCart = new Cart();
        newCart.setIdCart(UUID.randomUUID().toString());
        newCart.setUser(user);

        Date now = new Date();
        newCart.setCreatedAt(new Timestamp(now.getTime()));
        newCart.setUpdatedAt(new Timestamp(now.getTime()));

        return cartRepository.save(newCart);
    }

    /**
     * Convierte CartItem a DtoCartItemWithDetails
     */
    private DtoCartItemWithDetails convertToItemWithDetails(CartItem item) {
        DtoCartItemWithDetails dto = new DtoCartItemWithDetails();

        // Datos básicos del item
        dto.setIdCartItem(item.getIdCartItem());
        dto.setIdCart(item.getCart().getIdCart());
        dto.setIdProduct(item.getProduct().getIdProduct());
        dto.setIdVariant(item.getVariant().getIdVariant());
        dto.setQuantity(item.getQuantity());
        dto.setUnitPrice(item.getUnitPrice());
        dto.setCreatedAt(item.getCreatedAt());
        dto.setUpdatedAt(item.getUpdatedAt());

        // Agregar detalles del producto
        DtoProduct dtoProduct = productBusiness.findById(item.getProduct().getIdProduct());
        dto.setProduct(dtoProduct);

        // Agregar detalles de la variante
        DtoProductVariant dtoVariant = productVariantBusiness.findById(item.getVariant().getIdVariant());
        dto.setVariant(dtoVariant);

        return dto;
    }
}
