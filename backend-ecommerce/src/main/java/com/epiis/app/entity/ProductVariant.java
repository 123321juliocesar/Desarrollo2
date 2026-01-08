package com.epiis.app.entity;

import com.epiis.app.generic.EntityGeneric;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tProductVariant")
public class ProductVariant extends EntityGeneric {

    @Id
    @Column(name = "idVariant", length = 36, nullable = false)
    private String idVariant;

    @ManyToOne
    @JoinColumn(name = "idProduct", nullable = false)
    private Product product;

    @Column(name = "size", length = 20)
    private String size;

    @Column(name = "color", length = 50)
    private String color;

    @Column(name = "colorHex", length = 7)
    private String colorHex;

    @Column(name = "stock", nullable = false)
    private Integer stock;
}
