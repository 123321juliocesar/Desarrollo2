package com.epiis.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.epiis.app.entity.Brand;

@Repository
public interface BrandRepository extends JpaRepository<Brand, String> {
    boolean existsByName(String name);

    List<Brand> findByNameContainingIgnoreCase(String name);
}
