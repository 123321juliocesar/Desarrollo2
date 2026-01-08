package com.epiis.app.business;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epiis.app.dto.DtoCategory;
import com.epiis.app.entity.Category;
import com.epiis.app.repository.CategoryRepository;

@Service
public class CategoryBusiness {

    @Autowired
    private CategoryRepository categoryRepository;

    /**
     * Crea una nueva categoría
     * 
     * @param dtoCategory - Datos de la categoría a crear
     * @return String - Mensaje de resultado
     */
    public String create(DtoCategory dtoCategory) {
        // Validación: Nombre no puede estar vacío
        if (dtoCategory.getName() == null || dtoCategory.getName().trim().isEmpty()) {
            return "El nombre de la categoría es obligatorio";
        }

        // Validación: Nombre no puede estar duplicado
        if (categoryRepository.existsByName(dtoCategory.getName().trim())) {
            return "Ya existe una categoría con ese nombre";
        }

        // Generar ID y fechas
        dtoCategory.setIdCategory(UUID.randomUUID().toString());
        dtoCategory.setCreatedAt(new Date());
        dtoCategory.setUpdatedAt(dtoCategory.getCreatedAt());
        dtoCategory.setProductCount(0);

        // Crear entidad Category
        Category category = new Category();
        category.setIdCategory(dtoCategory.getIdCategory());
        category.setName(dtoCategory.getName().trim());
        category.setDescription(dtoCategory.getDescription() != null ? dtoCategory.getDescription().trim() : null);
        category.setProductCount(0);
        category.setCreatedAt(new Timestamp(dtoCategory.getCreatedAt().getTime()));
        category.setUpdatedAt(new Timestamp(dtoCategory.getUpdatedAt().getTime()));

        // Guardar en base de datos
        categoryRepository.save(category);

        return "Categoría creada correctamente";
    }

    /**
     * Obtiene todas las categorías
     * 
     * @return List<DtoCategory> - Lista de categorías
     */
    public List<DtoCategory> findAll() {
        return categoryRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Busca una categoría por ID
     * 
     * @param idCategory - ID de la categoría
     * @return DtoCategory - Categoría encontrada o null
     */
    public DtoCategory findById(String idCategory) {
        Optional<Category> categoryOptional = categoryRepository.findById(idCategory);
        return categoryOptional.map(this::convertToDto).orElse(null);
    }

    /**
     * Actualiza una categoría
     * 
     * @param idCategory  - ID de la categoría a actualizar
     * @param dtoCategory - Datos actualizados
     * @return String - Mensaje de resultado
     */
    public String update(String idCategory, DtoCategory dtoCategory) {
        Optional<Category> categoryOptional = categoryRepository.findById(idCategory);

        if (!categoryOptional.isPresent()) {
            return "Categoría no encontrada";
        }

        Category category = categoryOptional.get();

        // Validar nombre si cambió
        if (dtoCategory.getName() != null && !dtoCategory.getName().trim().isEmpty()) {
            if (!category.getName().equals(dtoCategory.getName().trim())) {
                if (categoryRepository.existsByName(dtoCategory.getName().trim())) {
                    return "Ya existe una categoría con ese nombre";
                }
                category.setName(dtoCategory.getName().trim());
            }
        }

        // Actualizar descripción
        if (dtoCategory.getDescription() != null) {
            category.setDescription(dtoCategory.getDescription().trim());
        }

        category.setUpdatedAt(new Timestamp(new Date().getTime()));

        categoryRepository.save(category);

        return "Categoría actualizada correctamente";
    }

    /**
     * Elimina una categoría
     * 
     * @param idCategory - ID de la categoría
     * @return String - Mensaje de resultado
     */
    public String delete(String idCategory) {
        Optional<Category> categoryOptional = categoryRepository.findById(idCategory);

        if (!categoryOptional.isPresent()) {
            return "Categoría no encontrada";
        }

        categoryRepository.deleteById(idCategory);

        return "Categoría eliminada correctamente";
    }

    /**
     * Busca categorías por nombre
     * 
     * @param name - Nombre a buscar
     * @return List<DtoCategory> - Lista de categorías que coinciden
     */
    public List<DtoCategory> search(String name) {
        return categoryRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Convierte una entidad Category a DtoCategory
     * 
     * @param category - Entidad Category
     * @return DtoCategory
     */
    private DtoCategory convertToDto(Category category) {
        DtoCategory dtoCategory = new DtoCategory();
        dtoCategory.setIdCategory(category.getIdCategory());
        dtoCategory.setName(category.getName());
        dtoCategory.setDescription(category.getDescription());
        dtoCategory.setProductCount(category.getProductCount());
        dtoCategory.setCreatedAt(category.getCreatedAt());
        dtoCategory.setUpdatedAt(category.getUpdatedAt());

        return dtoCategory;
    }
}
