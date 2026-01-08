package com.epiis.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.epiis.app.entity.ProductVariant;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, String> {

    // Busca variantes por producto
    List<ProductVariant> findByProduct_IdProduct(String idProduct);

    // Busca variantes por talla
    List<ProductVariant> findBySize(String size);

    // Busca variantes por color
    List<ProductVariant> findByColor(String color);
}
