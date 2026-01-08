package com.epiis.app.entity;

import com.epiis.app.generic.EntityGeneric;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tcategory")
@Getter
@Setter
public class Category extends EntityGeneric {

    @Id
    @Column(name = "idCategory", length = 36)
    private String idCategory;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "productCount")
    private Integer productCount = 0;
}
