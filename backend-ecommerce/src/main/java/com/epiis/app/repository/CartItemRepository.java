package com.epiis.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.epiis.app.entity.CartItem;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, String> {

    /**
     * Busca todos los items de un carrito específico
     * 
     * @param idCart - ID del carrito
     * @return Lista de items del carrito
     */
    List<CartItem> findByCart_IdCart(String idCart);

    /**
     * Busca un item específico por carrito y variante
     * 
     * @param idCart    - ID del carrito
     * @param idVariant - ID de la variante del producto
     * @return Optional con el item si existe
     */
    Optional<CartItem> findByCart_IdCartAndVariant_IdVariant(String idCart, String idVariant);

    /**
     * Elimina todos los items de un carrito
     * 
     * @param idCart - ID del carrito
     */
    void deleteByCart_IdCart(String idCart);
}
