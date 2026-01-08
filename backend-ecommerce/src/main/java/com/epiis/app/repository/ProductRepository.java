package com.epiis.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.epiis.app.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, String>, JpaSpecificationExecutor<Product> {

    // Busca productos por categoría
    List<Product> findByCategory_IdCategory(String idCategory);

    // Busca productos por marca
    List<Product> findByBrand_IdBrand(String idBrand);

    // Busca productos por nombre (case insensitive)
    List<Product> findByNameContainingIgnoreCase(String name);

    // Busca productos por estado
    List<Product> findByStatus(String status);
}
