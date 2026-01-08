package com.epiis.app.entity;

import java.math.BigDecimal;
import java.util.List;

import com.epiis.app.generic.EntityGeneric;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tProduct")
public class Product extends EntityGeneric {

    @Id
    @Column(name = "idProduct", length = 36, nullable = false)
    private String idProduct;

    @ManyToOne
    @JoinColumn(name = "idCategory", nullable = false)
    private Category category;

    @ManyToOne
    @JoinColumn(name = "idBrand", nullable = false)
    private Brand brand;

    @Column(name = "name", length = 150, nullable = false)
    private String name;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "price", precision = 10, scale = 2, nullable = false)
    private BigDecimal price;

    @Column(name = "oldPrice", precision = 10, scale = 2)
    private BigDecimal oldPrice;

    @Column(name = "imageUrl", length = 500)
    private String imageUrl;

    @Column(name = "status", length = 20)
    private String status = "active";

    @Column(name = "averageRating", precision = 3, scale = 2)
    private BigDecimal averageRating = BigDecimal.ZERO;

    @Column(name = "reviewCount")
    private Integer reviewCount = 0;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductVariant> variants;
}
