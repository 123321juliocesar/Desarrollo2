package com.epiis.app.business;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epiis.app.dto.DtoProduct;
import com.epiis.app.dto.DtoProductDetail;
import com.epiis.app.dto.DtoProductVariant;
import com.epiis.app.entity.Brand;
import com.epiis.app.entity.Category;
import com.epiis.app.entity.Product;
import com.epiis.app.entity.ProductVariant;
import com.epiis.app.repository.BrandRepository;
import com.epiis.app.repository.CategoryRepository;
import com.epiis.app.repository.ProductRepository;

@Service
public class ProductBusiness {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BrandRepository brandRepository;

    /**
     * Crea un nuevo producto
     */
    public String create(DtoProduct dtoProduct) {
        // Validación: Nombre obligatorio
        if (dtoProduct.getName() == null || dtoProduct.getName().trim().isEmpty()) {
            return "El nombre del producto es obligatorio";
        }

        // Validación: Precio obligatorio y mayor a 0
        if (dtoProduct.getPrice() == null || dtoProduct.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            return "El precio debe ser mayor a 0";
        }

        // Validación: Categoría obligatoria y debe existir
        if (dtoProduct.getIdCategory() == null || dtoProduct.getIdCategory().trim().isEmpty()) {
            return "La categoría es obligatoria";
        }

        Optional<Category> categoryOptional = categoryRepository.findById(dtoProduct.getIdCategory());
        if (!categoryOptional.isPresent()) {
            return "La categoría especificada no existe";
        }

        // Validación: Marca obligatoria y debe existir
        if (dtoProduct.getIdBrand() == null || dtoProduct.getIdBrand().trim().isEmpty()) {
            return "La marca es obligatoria";
        }

        Optional<Brand> brandOptional = brandRepository.findById(dtoProduct.getIdBrand());
        if (!brandOptional.isPresent()) {
            return "La marca especificada no existe";
        }

        // Validación: Status válido
        if (dtoProduct.getStatus() != null && !dtoProduct.getStatus().trim().isEmpty()) {
            String status = dtoProduct.getStatus().toLowerCase();
            if (!status.equals("active") && !status.equals("inactive") && !status.equals("out_of_stock")) {
                return "El estado debe ser: active, inactive o out_of_stock";
            }
        }

        // Generar ID y fechas
        dtoProduct.setIdProduct(UUID.randomUUID().toString());
        dtoProduct.setCreatedAt(new Date());
        dtoProduct.setUpdatedAt(dtoProduct.getCreatedAt());

        // Valores por defecto
        if (dtoProduct.getStatus() == null || dtoProduct.getStatus().trim().isEmpty()) {
            dtoProduct.setStatus("active");
        }
        if (dtoProduct.getAverageRating() == null) {
            dtoProduct.setAverageRating(BigDecimal.ZERO);
        }
        if (dtoProduct.getReviewCount() == null) {
            dtoProduct.setReviewCount(0);
        }

        // Crear entidad Product
        Product product = new Product();
        product.setIdProduct(dtoProduct.getIdProduct());
        product.setCategory(categoryOptional.get());
        product.setBrand(brandOptional.get());
        product.setName(dtoProduct.getName().trim());
        product.setDescription(dtoProduct.getDescription() != null ? dtoProduct.getDescription().trim() : null);
        product.setPrice(dtoProduct.getPrice());
        product.setOldPrice(dtoProduct.getOldPrice());
        product.setImageUrl(dtoProduct.getImageUrl() != null ? dtoProduct.getImageUrl().trim() : null);
        product.setStatus(dtoProduct.getStatus());
        product.setAverageRating(dtoProduct.getAverageRating());
        product.setReviewCount(dtoProduct.getReviewCount());
        product.setCreatedAt(new Timestamp(dtoProduct.getCreatedAt().getTime()));
        product.setUpdatedAt(new Timestamp(dtoProduct.getUpdatedAt().getTime()));

        // Guardar en base de datos
        productRepository.save(product);

        return "Producto creado correctamente";
    }

    /**
     * Obtiene todos los productos
     */
    public List<DtoProduct> findAll() {
        return productRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Busca un producto por ID
     */
    public DtoProduct findById(String idProduct) {
        Optional<Product> productOptional = productRepository.findById(idProduct);
        return productOptional.map(this::convertToDto).orElse(null);
    }

    /**
     * Busca un producto por ID con todas sus variantes
     * 
     * @param idProduct - ID del producto
     * @return DtoProductDetail con el producto y sus variantes, o null si no existe
     */
    public DtoProductDetail findByIdWithVariants(String idProduct) {
        Optional<Product> productOptional = productRepository.findById(idProduct);

        if (!productOptional.isPresent()) {
            return null;
        }

        Product product = productOptional.get();

        // Convertir producto a DTO detallado
        DtoProductDetail dtoProductDetail = new DtoProductDetail();

        // Copiar datos del producto
        DtoProduct dtoProduct = convertToDto(product);
        dtoProductDetail.setIdProduct(dtoProduct.getIdProduct());
        dtoProductDetail.setIdCategory(dtoProduct.getIdCategory());
        dtoProductDetail.setIdBrand(dtoProduct.getIdBrand());
        dtoProductDetail.setName(dtoProduct.getName());
        dtoProductDetail.setDescription(dtoProduct.getDescription());
        dtoProductDetail.setPrice(dtoProduct.getPrice());
        dtoProductDetail.setOldPrice(dtoProduct.getOldPrice());
        dtoProductDetail.setImageUrl(dtoProduct.getImageUrl());
        dtoProductDetail.setStatus(dtoProduct.getStatus());
        dtoProductDetail.setAverageRating(dtoProduct.getAverageRating());
        dtoProductDetail.setReviewCount(dtoProduct.getReviewCount());
        dtoProductDetail.setCreatedAt(dtoProduct.getCreatedAt());
        dtoProductDetail.setUpdatedAt(dtoProduct.getUpdatedAt());

        // Obtener variantes del producto
        List<DtoProductVariant> variants = product.getVariants().stream()
                .map(this::convertVariantToDto)
                .collect(Collectors.toList());

        dtoProductDetail.setVariants(variants);

        return dtoProductDetail;
    }

    /**
     * Convierte una entidad ProductVariant a DtoProductVariant
     */
    private DtoProductVariant convertVariantToDto(ProductVariant variant) {
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

    /**
     * Actualiza un producto
     */
    public String update(String idProduct, DtoProduct dtoProduct) {
        Optional<Product> productOptional = productRepository.findById(idProduct);

        if (!productOptional.isPresent()) {
            return "Producto no encontrado";
        }

        Product product = productOptional.get();

        // Actualizar nombre si se proporciona
        if (dtoProduct.getName() != null && !dtoProduct.getName().trim().isEmpty()) {
            product.setName(dtoProduct.getName().trim());
        }

        // Actualizar descripción
        if (dtoProduct.getDescription() != null) {
            product.setDescription(dtoProduct.getDescription().trim());
        }

        // Actualizar precio
        if (dtoProduct.getPrice() != null) {
            if (dtoProduct.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                return "El precio debe ser mayor a 0";
            }
            product.setPrice(dtoProduct.getPrice());
        }

        // Actualizar oldPrice
        if (dtoProduct.getOldPrice() != null) {
            product.setOldPrice(dtoProduct.getOldPrice());
        }

        // Actualizar imageUrl
        if (dtoProduct.getImageUrl() != null) {
            product.setImageUrl(dtoProduct.getImageUrl().trim());
        }

        // Actualizar status
        if (dtoProduct.getStatus() != null && !dtoProduct.getStatus().trim().isEmpty()) {
            String status = dtoProduct.getStatus().toLowerCase();
            if (!status.equals("active") && !status.equals("inactive") && !status.equals("out_of_stock")) {
                return "El estado debe ser: active, inactive o out_of_stock";
            }
            product.setStatus(status);
        }

        // Actualizar categoría si se proporciona
        if (dtoProduct.getIdCategory() != null && !dtoProduct.getIdCategory().trim().isEmpty()) {
            Optional<Category> categoryOptional = categoryRepository.findById(dtoProduct.getIdCategory());
            if (!categoryOptional.isPresent()) {
                return "La categoría especificada no existe";
            }
            product.setCategory(categoryOptional.get());
        }

        // Actualizar marca si se proporciona
        if (dtoProduct.getIdBrand() != null && !dtoProduct.getIdBrand().trim().isEmpty()) {
            Optional<Brand> brandOptional = brandRepository.findById(dtoProduct.getIdBrand());
            if (!brandOptional.isPresent()) {
                return "La marca especificada no existe";
            }
            product.setBrand(brandOptional.get());
        }

        product.setUpdatedAt(new Timestamp(new Date().getTime()));

        productRepository.save(product);

        return "Producto actualizado correctamente";
    }

    /**
     * Elimina un producto
     */
    public String delete(String idProduct) {
        Optional<Product> productOptional = productRepository.findById(idProduct);

        if (!productOptional.isPresent()) {
            return "Producto no encontrado";
        }

        productRepository.deleteById(idProduct);

        return "Producto eliminado correctamente";
    }

    /**
     * Busca productos por nombre
     */
    public List<DtoProduct> search(String name) {
        if (name == null || name.trim().isEmpty()) {
            return findAll();
        }

        return productRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Busca productos por categoría
     */
    public List<DtoProduct> findByCategory(String idCategory) {
        return productRepository.findByCategory_IdCategory(idCategory).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Busca productos por marca
     */
    public List<DtoProduct> findByBrand(String idBrand) {
        return productRepository.findByBrand_IdBrand(idBrand).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Busca productos con filtros combinados (solo productos activos)
     * 
     * @param search     - Texto para buscar en nombre o descripción
     * @param idCategory - ID de categoría (opcional)
     * @param idBrand    - ID de marca (opcional)
     * @param sizes      - Lista de tallas separadas por coma (opcional)
     * @param colors     - Lista de colores separados por coma (opcional)
     * @param minPrice   - Precio mínimo (opcional)
     * @param maxPrice   - Precio máximo (opcional)
     * @param sortBy     - Criterio de ordenamiento: featured, priceAsc, priceDesc
     *                   (opcional)
     * @return Lista de productos que cumplen los criterios
     */
    public List<DtoProduct> findWithFilters(String search, String idCategory, String idBrand,
            String sizes, String colors,
            BigDecimal minPrice, BigDecimal maxPrice,
            String sortBy) {
        return productRepository.findAll((root, query, criteriaBuilder) -> {
            var predicates = new java.util.ArrayList<jakarta.persistence.criteria.Predicate>();

            // Filtro: Solo productos activos
            predicates.add(criteriaBuilder.equal(root.get("status"), "active"));

            // Filtro: Búsqueda en nombre o descripción
            if (search != null && !search.trim().isEmpty()) {
                String searchPattern = "%" + search.toLowerCase() + "%";
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), searchPattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), searchPattern)));
            }

            // Filtro: Categoría
            if (idCategory != null && !idCategory.trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("category").get("idCategory"), idCategory));
            }

            // Filtro: Marca
            if (idBrand != null && !idBrand.trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("brand").get("idBrand"), idBrand));
            }

            // Filtro: Rango de precios
            if (minPrice != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice));
            }
            if (maxPrice != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice));
            }

            // Filtro: Tallas y Colores (requiere JOIN con ProductVariant)
            if ((sizes != null && !sizes.trim().isEmpty()) || (colors != null && !colors.trim().isEmpty())) {
                var variantJoin = root.join("variants", jakarta.persistence.criteria.JoinType.INNER);

                if (sizes != null && !sizes.trim().isEmpty()) {
                    String[] sizeArray = sizes.split(",");
                    var sizePredicates = new java.util.ArrayList<jakarta.persistence.criteria.Predicate>();
                    for (String size : sizeArray) {
                        sizePredicates.add(criteriaBuilder.equal(variantJoin.get("size"), size.trim()));
                    }
                    predicates.add(
                            criteriaBuilder.or(sizePredicates.toArray(new jakarta.persistence.criteria.Predicate[0])));
                }

                if (colors != null && !colors.trim().isEmpty()) {
                    String[] colorArray = colors.split(",");
                    var colorPredicates = new java.util.ArrayList<jakarta.persistence.criteria.Predicate>();
                    for (String color : colorArray) {
                        colorPredicates.add(criteriaBuilder.equal(variantJoin.get("color"), color.trim()));
                    }
                    predicates.add(
                            criteriaBuilder.or(colorPredicates.toArray(new jakarta.persistence.criteria.Predicate[0])));
                }

                // Asegurar que no haya duplicados cuando hay variantes
                query.distinct(true);
            }

            // Ordenamiento
            if (sortBy != null && !sortBy.trim().isEmpty()) {
                switch (sortBy.toLowerCase()) {
                    case "priceasc":
                        query.orderBy(criteriaBuilder.asc(root.get("price")));
                        break;
                    case "pricedesc":
                        query.orderBy(criteriaBuilder.desc(root.get("price")));
                        break;
                    case "featured":
                    default:
                        // Ordenar por calificación descendente y luego por fecha de creación
                        query.orderBy(
                                criteriaBuilder.desc(root.get("averageRating")),
                                criteriaBuilder.desc(root.get("createdAt")));
                        break;
                }
            } else {
                // Ordenamiento por defecto: featured
                query.orderBy(
                        criteriaBuilder.desc(root.get("averageRating")),
                        criteriaBuilder.desc(root.get("createdAt")));
            }

            return criteriaBuilder.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        }).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Busca productos por estado
     */
    public List<DtoProduct> findByStatus(String status) {
        return productRepository.findByStatus(status).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Convierte una entidad Product a DtoProduct
     */
    private DtoProduct convertToDto(Product product) {
        DtoProduct dtoProduct = new DtoProduct();
        dtoProduct.setIdProduct(product.getIdProduct());
        dtoProduct.setIdCategory(product.getCategory().getIdCategory());
        dtoProduct.setIdBrand(product.getBrand().getIdBrand());
        dtoProduct.setName(product.getName());
        dtoProduct.setDescription(product.getDescription());
        dtoProduct.setPrice(product.getPrice());
        dtoProduct.setOldPrice(product.getOldPrice());
        dtoProduct.setImageUrl(product.getImageUrl());
        dtoProduct.setStatus(product.getStatus());
        dtoProduct.setAverageRating(product.getAverageRating());
        dtoProduct.setReviewCount(product.getReviewCount());
        dtoProduct.setCreatedAt(product.getCreatedAt());
        dtoProduct.setUpdatedAt(product.getUpdatedAt());

        return dtoProduct;
    }
}
