package com.epiis.app.business;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epiis.app.dto.DtoProductVariant;
import com.epiis.app.entity.Product;
import com.epiis.app.entity.ProductVariant;
import com.epiis.app.repository.ProductRepository;
import com.epiis.app.repository.ProductVariantRepository;

@Service
public class ProductVariantBusiness {

    @Autowired
    private ProductVariantRepository productVariantRepository;

    @Autowired
    private ProductRepository productRepository;

    // Crea una nueva variante de producto
    public String create(DtoProductVariant dtoVariant) {
        // Validación: Producto obligatorio y debe existir
        if (dtoVariant.getIdProduct() == null || dtoVariant.getIdProduct().trim().isEmpty()) {
            return "El producto es obligatorio";
        }

        Optional<Product> productOptional = productRepository.findById(dtoVariant.getIdProduct());
        if (!productOptional.isPresent()) {
            return "El producto especificado no existe";
        }

        // Validación: Al menos talla o color debe estar presente
        if ((dtoVariant.getSize() == null || dtoVariant.getSize().trim().isEmpty()) &&
                (dtoVariant.getColor() == null || dtoVariant.getColor().trim().isEmpty())) {
            return "Debe especificar al menos una talla o un color";
        }

        // Validación: Stock obligatorio y no negativo
        if (dtoVariant.getStock() == null || dtoVariant.getStock() < 0) {
            return "El stock debe ser mayor o igual a 0";
        }

        // Generar ID y fechas
        dtoVariant.setIdVariant(UUID.randomUUID().toString());
        dtoVariant.setCreatedAt(new Date());
        dtoVariant.setUpdatedAt(dtoVariant.getCreatedAt());

        // Crear entidad ProductVariant
        ProductVariant variant = new ProductVariant();
        variant.setIdVariant(dtoVariant.getIdVariant());
        variant.setProduct(productOptional.get());
        variant.setSize(dtoVariant.getSize() != null ? dtoVariant.getSize().trim() : null);
        variant.setColor(dtoVariant.getColor() != null ? dtoVariant.getColor().trim() : null);
        variant.setColorHex(dtoVariant.getColorHex() != null ? dtoVariant.getColorHex().trim() : null);
        variant.setStock(dtoVariant.getStock());
        variant.setCreatedAt(new Timestamp(dtoVariant.getCreatedAt().getTime()));
        variant.setUpdatedAt(new Timestamp(dtoVariant.getUpdatedAt().getTime()));

        // Guardar en base de datos
        productVariantRepository.save(variant);

        return "Variante creada correctamente";
    }

    /**
     * Obtiene todas las variantes
     */
    public List<DtoProductVariant> findAll() {
        return productVariantRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Busca una variante por ID
     */
    public DtoProductVariant findById(String idVariant) {
        Optional<ProductVariant> variantOptional = productVariantRepository.findById(idVariant);
        return variantOptional.map(this::convertToDto).orElse(null);
    }

    /**
     * Actualiza una variante
     */
    public String update(String idVariant, DtoProductVariant dtoVariant) {
        Optional<ProductVariant> variantOptional = productVariantRepository.findById(idVariant);

        if (!variantOptional.isPresent()) {
            return "Variante no encontrada";
        }

        ProductVariant variant = variantOptional.get();

        // Actualizar talla
        if (dtoVariant.getSize() != null) {
            variant.setSize(dtoVariant.getSize().trim());
        }

        // Actualizar color
        if (dtoVariant.getColor() != null) {
            variant.setColor(dtoVariant.getColor().trim());
        }

        // Actualizar colorHex
        if (dtoVariant.getColorHex() != null) {
            variant.setColorHex(dtoVariant.getColorHex().trim());
        }

        // Actualizar stock
        if (dtoVariant.getStock() != null) {
            if (dtoVariant.getStock() < 0) {
                return "El stock debe ser mayor o igual a 0";
            }
            variant.setStock(dtoVariant.getStock());
        }

        // Actualizar producto si se proporciona
        if (dtoVariant.getIdProduct() != null && !dtoVariant.getIdProduct().trim().isEmpty()) {
            Optional<Product> productOptional = productRepository.findById(dtoVariant.getIdProduct());
            if (!productOptional.isPresent()) {
                return "El producto especificado no existe";
            }
            variant.setProduct(productOptional.get());
        }

        variant.setUpdatedAt(new Timestamp(new Date().getTime()));

        productVariantRepository.save(variant);

        return "Variante actualizada correctamente";
    }

    /**
     * Elimina una variante
     */
    public String delete(String idVariant) {
        Optional<ProductVariant> variantOptional = productVariantRepository.findById(idVariant);

        if (!variantOptional.isPresent()) {
            return "Variante no encontrada";
        }

        productVariantRepository.deleteById(idVariant);

        return "Variante eliminada correctamente";
    }

    /**
     * Busca variantes por producto
     */
    public List<DtoProductVariant> findByProduct(String idProduct) {
        return productVariantRepository.findByProduct_IdProduct(idProduct).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Convierte una entidad ProductVariant a DtoProductVariant
     */
    private DtoProductVariant convertToDto(ProductVariant variant) {
        DtoProductVariant dtoVariant = new DtoProductVariant();
        dtoVariant.setIdVariant(variant.getIdVariant());
        dtoVariant.setIdProduct(variant.getProduct().getIdProduct());
        dtoVariant.setSize(variant.getSize());
        dtoVariant.setColor(variant.getColor());
        dtoVariant.setColorHex(variant.getColorHex());
        dtoVariant.setStock(variant.getStock());
        dtoVariant.setCreatedAt(variant.getCreatedAt());
        dtoVariant.setUpdatedAt(variant.getUpdatedAt());

        return dtoVariant;
    }
}
