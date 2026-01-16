package com.epiis.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.epiis.app.entity.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart, String> {

    /**
     * Busca el carrito de un usuario específico
     * 
     * @param idUser - ID del usuario
     * @return Optional con el carrito si existe
     */
    Optional<Cart> findByUser_IdUser(String idUser);
}
