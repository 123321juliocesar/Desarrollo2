package com.epiis.app.entity;

import com.epiis.app.generic.EntityGeneric;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tcategory")
@Getter
@Setter
public class Cart extends EntityGeneric {

    @Id
    @Column(name = "idCart", length = 36)
    private String idCart;

    @ManyToOne
    @JoinColumn(name = "idUser", nullable = false)
    private Category Iduser;

}
