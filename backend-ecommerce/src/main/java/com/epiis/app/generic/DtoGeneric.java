package com.epiis.app.generic;

import java.util.Date;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;


@MappedSuperclass
@Getter
@Setter
public abstract class DtoGeneric {
	private Date createdAt;
	private Date updatedAt;
}