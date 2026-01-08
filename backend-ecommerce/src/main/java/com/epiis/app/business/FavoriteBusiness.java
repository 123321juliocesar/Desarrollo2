package com.epiis.app.business;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.epiis.app.dto.DtoFavorite;
import com.epiis.app.dto.DtoFavoriteToggleResult;
import com.epiis.app.dto.DtoFavoriteWithProduct;
import com.epiis.app.dto.DtoProduct;
import com.epiis.app.entity.Favorite;
import com.epiis.app.entity.Product;
import com.epiis.app.entity.User;
import com.epiis.app.repository.FavoriteRepository;
import com.epiis.app.repository.ProductRepository;
import com.epiis.app.repository.UserRepository;

@Service
public class FavoriteBusiness {

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductBusiness productBusiness;

    /**
     * Toggle favorito: agrega si no existe, elimina si existe
     * 
     * @param idUser    - ID del usuario
     * @param idProduct - ID del producto
     * @return DtoFavoriteToggleResult con action, message y favorite (si fue
     *         agregado)
     */
    @Transactional
    public DtoFavoriteToggleResult toggleFavorite(String idUser, String idProduct) {
        DtoFavoriteToggleResult result = new DtoFavoriteToggleResult();

        // Validar usuario
        Optional<User> userOptional = userRepository.findById(idUser);
        if (!userOptional.isPresent()) {
            result.setError("Usuario no encontrado");
            return result;
        }

        // Validar producto
        Optional<Product> productOptional = productRepository.findById(idProduct);
        if (!productOptional.isPresent()) {
            result.setError("Producto no encontrado");
            return result;
        }

        Product product = productOptional.get();

        // Validar que el producto esté activo
        if (!"active".equals(product.getStatus())) {
            result.setError("El producto no está disponible");
            return result;
        }

        // Verificar si ya existe el favorito
        Optional<Favorite> existingFavorite = favoriteRepository.findByUser_IdUserAndProduct_IdProduct(idUser,
                idProduct);

        if (existingFavorite.isPresent()) {
            // Ya existe: eliminar
            favoriteRepository.delete(existingFavorite.get());
            result.setAction("removed");
            result.setMessage("Producto eliminado de favoritos");
        } else {
            // No existe: agregar
            Favorite favorite = new Favorite();
            favorite.setIdFavorite(UUID.randomUUID().toString());
            favorite.setUser(userOptional.get());
            favorite.setProduct(product);

            Date now = new Date();
            favorite.setCreatedAt(new Timestamp(now.getTime()));
            favorite.setUpdatedAt(new Timestamp(now.getTime()));

            favoriteRepository.save(favorite);

            DtoFavorite dtoFavorite = convertToDto(favorite);
            result.setAction("added");
            result.setFavorite(dtoFavorite);
            result.setMessage("Producto agregado a favoritos");
        }

        return result;
    }

    /**
     * Obtiene todos los favoritos de un usuario con información del producto
     * 
     * @param idUser - ID del usuario
     * @return Lista de favoritos con productos
     */
    public List<DtoFavoriteWithProduct> getFavoritesByUser(String idUser) {
        List<Favorite> favorites = favoriteRepository.findByUser_IdUser(idUser);

        return favorites.stream()
                .filter(fav -> "active".equals(fav.getProduct().getStatus())) // Solo productos activos
                .map(this::convertToDtoWithProduct)
                .collect(Collectors.toList());
    }

    /**
     * Verifica si un producto está en favoritos
     * 
     * @param idUser    - ID del usuario
     * @param idProduct - ID del producto
     * @return true si está en favoritos, false si no
     */
    public boolean checkFavorite(String idUser, String idProduct) {
        return favoriteRepository.existsByUser_IdUserAndProduct_IdProduct(idUser, idProduct);
    }

    /**
     * Elimina un favorito específico
     * 
     * @param idUser    - ID del usuario
     * @param idProduct - ID del producto
     * @return Mensaje de resultado
     */
    @Transactional
    public String removeFavorite(String idUser, String idProduct) {
        Optional<Favorite> favoriteOptional = favoriteRepository.findByUser_IdUserAndProduct_IdProduct(idUser,
                idProduct);

        if (!favoriteOptional.isPresent()) {
            return "Favorito no encontrado";
        }

        favoriteRepository.delete(favoriteOptional.get());
        return "Producto eliminado de favoritos";
    }

    /**
     * Convierte una entidad Favorite a DtoFavorite
     */
    private DtoFavorite convertToDto(Favorite favorite) {
        DtoFavorite dto = new DtoFavorite();
        dto.setIdFavorite(favorite.getIdFavorite());
        dto.setIdUser(favorite.getUser().getIdUser());
        dto.setIdProduct(favorite.getProduct().getIdProduct());
        dto.setCreatedAt(favorite.getCreatedAt());
        dto.setUpdatedAt(favorite.getUpdatedAt());
        return dto;
    }

    /**
     * Convierte una entidad Favorite a DtoFavoriteWithProduct
     */
    private DtoFavoriteWithProduct convertToDtoWithProduct(Favorite favorite) {
        DtoFavoriteWithProduct dto = new DtoFavoriteWithProduct();
        dto.setIdFavorite(favorite.getIdFavorite());
        dto.setIdUser(favorite.getUser().getIdUser());
        dto.setIdProduct(favorite.getProduct().getIdProduct());
        dto.setCreatedAt(favorite.getCreatedAt());
        dto.setUpdatedAt(favorite.getUpdatedAt());

        // Agregar información del producto
        DtoProduct dtoProduct = productBusiness.findById(favorite.getProduct().getIdProduct());
        dto.setProduct(dtoProduct);

        return dto;
    }
}
