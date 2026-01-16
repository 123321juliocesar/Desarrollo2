package com.epiis.app.entity;

import java.time.LocalDate;
import com.epiis.app.generic.EntityGeneric;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tuser")
@Getter
@Setter
public class User extends EntityGeneric {

    @Id
    @Column(name = "idUser", length = 36)
    private String idUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idRole")
    private Role role;

    @Column(name = "firstName", nullable = false, length = 70)
    private String firstName;

    @Column(name = "lastName", nullable = false, length = 70)
    private String lastName;

    @Column(name = "email", unique = true, nullable = false, length = 150)
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "postalCode", length = 20)
    private String postalCode;

    @Column(name = "province", length = 100)
    private String province;

    @Column(name = "country", length = 100)
    private String country;

    @Column(name = "birthDate")
    private LocalDate birthDate; // Tipo LocalDate

    @Column(name = "isActive")
    private boolean isActive;

    @Column(name = "refreshToken", length = 500)
    private String refreshToken;
}