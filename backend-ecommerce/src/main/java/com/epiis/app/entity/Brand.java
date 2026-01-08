package com.epiis.app.entity;

import com.epiis.app.generic.EntityGeneric;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tbrand")
@Getter
@Setter
public class Brand extends EntityGeneric {

    @Id
    @Column(name = "idBrand", length = 36)
    private String idBrand;

    @Column(name = "name", nullable = false, length = 100)
    private String name;
}
