package com.epiis.app.entity;

import com.epiis.app.generic.EntityGeneric;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "treview")
public class Review extends EntityGeneric {

    @Id
    @Column(name = "idreview", length = 36, nullable = false)
    private String idReview;

    @ManyToOne
    @JoinColumn(name = "iduser", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "idproduct", nullable = false)
    private Product product;

    @Column(name = "rating", nullable = false)
    private Integer rating;

    @Column(name = "comment", length = 500)
    private String comment;

}
