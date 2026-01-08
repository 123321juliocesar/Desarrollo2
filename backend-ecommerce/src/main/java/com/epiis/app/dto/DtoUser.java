package com.epiis.app.dto;

import java.time.LocalDate;
import com.epiis.app.generic.DtoGeneric;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoUser extends DtoGeneric {
	private String idUser;
	private String idRole;
	private String firstName;
	private String lastName;
	private String email;
	private String password;
	private String phone;
	private String address;
	private String city;
	private String postalCode;
	private String province;
	private String country;
	private LocalDate birthDate;
	private boolean isActive;
}