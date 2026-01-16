package com.epiis.app.dto;

import java.time.LocalDate; // Importante: cambio a LocalDate
import java.util.Date;
import com.epiis.app.generic.DtoGeneric;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoUser extends DtoGeneric {

    private String idUser;
    private String idRole;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String phone;
    private String address;
    private String city;
    private String postalCode;
    private String province;
    private String country;
    private LocalDate birthDate; // Sincronizado con la Entidad
    private boolean active;
    private Date createdAt;
    private Date updatedAt;

    // Campos para la autenticación
    private String accessToken;
    private String refreshToken;
}