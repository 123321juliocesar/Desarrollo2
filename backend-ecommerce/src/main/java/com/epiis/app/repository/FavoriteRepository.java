package com.epiis.app.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.epiis.app.entity.Favorite;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, String> {

    // Busca todos los favoritos de un usuario
    List<Favorite> findByUser_IdUser(String idUser);

    // Busca un favorito específico por usuario y producto
    Optional<Favorite> findByUser_IdUserAndProduct_IdProduct(String idUser, String idProduct);

    // Verifica si existe un favorito para un usuario y producto
    boolean existsByUser_IdUserAndProduct_IdProduct(String idUser, String idProduct);

    // Elimina un favorito por usuario y producto
    void deleteByUser_IdUserAndProduct_IdProduct(String idUser, String idProduct);

    @Query("SELECT f.product.idProduct FROM Favorite f WHERE f.user.idUser = :idUser")
    Set<String> findProductIdsByUserId(@Param("idUser") String idUser);

}
