package com.epiis.app.business;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epiis.app.dto.DtoBrand;
import com.epiis.app.entity.Brand;
import com.epiis.app.repository.BrandRepository;

@Service
public class BrandBusiness {

    @Autowired
    private BrandRepository brandRepository;

    /**
     * Crea una nueva marca
     * 
     * @param dtoBrand - Datos de la marca a crear
     * @return String - Mensaje de resultado
     */
    public String create(DtoBrand dtoBrand) {
        // Validación: Nombre no puede estar vacío
        if (dtoBrand.getName() == null || dtoBrand.getName().trim().isEmpty()) {
            return "El nombre de la marca es obligatorio";
        }

        // Validación: Nombre no puede estar duplicado
        if (brandRepository.existsByName(dtoBrand.getName().trim())) {
            return "Ya existe una marca con ese nombre";
        }

        // Generar ID y fechas
        dtoBrand.setIdBrand(UUID.randomUUID().toString());
        dtoBrand.setCreatedAt(new Date());
        dtoBrand.setUpdatedAt(dtoBrand.getCreatedAt());

        // Crear entidad Brand
        Brand brand = new Brand();
        brand.setIdBrand(dtoBrand.getIdBrand());
        brand.setName(dtoBrand.getName().trim());
        brand.setCreatedAt(new Timestamp(dtoBrand.getCreatedAt().getTime()));
        brand.setUpdatedAt(new Timestamp(dtoBrand.getUpdatedAt().getTime()));

        // Guardar en base de datos
        brandRepository.save(brand);

        return "Marca creada correctamente";
    }

    /**
     * Obtiene todas las marcas
     * 
     * @return List<DtoBrand> - Lista de marcas
     */
    public List<DtoBrand> findAll() {
        return brandRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Busca una marca por ID
     * 
     * @param idBrand - ID de la marca
     * @return DtoBrand - Marca encontrada o null
     */
    public DtoBrand findById(String idBrand) {
        Optional<Brand> brandOptional = brandRepository.findById(idBrand);
        return brandOptional.map(this::convertToDto).orElse(null);
    }

    /**
     * Actualiza una marca
     * 
     * @param idBrand  - ID de la marca a actualizar
     * @param dtoBrand - Datos actualizados
     * @return String - Mensaje de resultado
     */
    public String update(String idBrand, DtoBrand dtoBrand) {
        Optional<Brand> brandOptional = brandRepository.findById(idBrand);

        if (!brandOptional.isPresent()) {
            return "Marca no encontrada";
        }

        Brand brand = brandOptional.get();

        // Validar nombre si cambió
        if (dtoBrand.getName() != null && !dtoBrand.getName().trim().isEmpty()) {
            if (!brand.getName().equals(dtoBrand.getName().trim())) {
                if (brandRepository.existsByName(dtoBrand.getName().trim())) {
                    return "Ya existe una marca con ese nombre";
                }
                brand.setName(dtoBrand.getName().trim());
            }
        }

        brand.setUpdatedAt(new Timestamp(new Date().getTime()));

        brandRepository.save(brand);

        return "Marca actualizada correctamente";
    }

    /**
     * Elimina una marca
     * 
     * @param idBrand - ID de la marca
     * @return String - Mensaje de resultado
     */
    public String delete(String idBrand) {
        Optional<Brand> brandOptional = brandRepository.findById(idBrand);

        if (!brandOptional.isPresent()) {
            return "Marca no encontrada";
        }

        brandRepository.deleteById(idBrand);

        return "Marca eliminada correctamente";
    }

    /**
     * Busca marcas por nombre
     * 
     * @param name - Nombre a buscar
     * @return List<DtoBrand> - Lista de marcas que coinciden
     */
    public List<DtoBrand> search(String name) {
        return brandRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Convierte una entidad Brand a DtoBrand
     * 
     * @param brand - Entidad Brand
     * @return DtoBrand
     */
    private DtoBrand convertToDto(Brand brand) {
        DtoBrand dtoBrand = new DtoBrand();
        dtoBrand.setIdBrand(brand.getIdBrand());
        dtoBrand.setName(brand.getName());
        dtoBrand.setCreatedAt(brand.getCreatedAt());
        dtoBrand.setUpdatedAt(brand.getUpdatedAt());

        return dtoBrand;
    }
}
