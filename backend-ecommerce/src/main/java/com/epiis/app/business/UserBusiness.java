package com.epiis.app.business;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.epiis.app.dto.DtoUser;
import com.epiis.app.entity.Role;
import com.epiis.app.entity.User;
import com.epiis.app.repository.RoleRepository;
import com.epiis.app.repository.UserRepository;

@Service
public class UserBusiness {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	/**
	 * Registra un nuevo usuario
	 * 
	 * @param dtoUser - Datos del usuario a registrar
	 * @return String - Mensaje de resultado
	 */
	public String register(DtoUser dtoUser) {
		// Validación: Email no puede estar vacío
		if (dtoUser.getEmail() == null || dtoUser.getEmail().trim().isEmpty()) {
			return "El email es obligatorio";
		}

		// Validación: Email debe tener formato válido
		if (!dtoUser.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
			return "El formato del email no es válido";
		}

		// Validación: Email no puede estar duplicado
		if (userRepository.existsByEmail(dtoUser.getEmail())) {
			return "El email ya está registrado";
		}

		// Validación: Password no puede estar vacío
		if (dtoUser.getPassword() == null || dtoUser.getPassword().trim().isEmpty()) {
			return "La contraseña es obligatoria";
		}

		// Validación: Password debe tener al menos 6 caracteres
		if (dtoUser.getPassword().length() < 6) {
			return "La contraseña debe tener al menos 6 caracteres";
		}

		// Validación: Nombre no puede estar vacío
		if (dtoUser.getFirstName() == null || dtoUser.getFirstName().trim().isEmpty()) {
			return "El nombre es obligatorio";
		}

		// Validación: Apellido no puede estar vacío
		if (dtoUser.getLastName() == null || dtoUser.getLastName().trim().isEmpty()) {
			return "El apellido es obligatorio";
		}

		// Validación: Role debe existir
		if (dtoUser.getIdRole() == null || dtoUser.getIdRole().trim().isEmpty()) {
			return "El rol es obligatorio";
		}

		Optional<Role> roleOptional = roleRepository.findById(dtoUser.getIdRole());
		if (!roleOptional.isPresent()) {
			return "El rol especificado no existe";
		}

		// Generar ID y fechas
		dtoUser.setIdUser(UUID.randomUUID().toString());
		dtoUser.setCreatedAt(new Date());
		dtoUser.setUpdatedAt(dtoUser.getCreatedAt());
		dtoUser.setActive(true);

		// Hashear la contraseña
		String hashedPassword = passwordEncoder.encode(dtoUser.getPassword());

		// Crear entidad User
		User user = new User();
		user.setIdUser(dtoUser.getIdUser());
		user.setRole(roleOptional.get());
		user.setEmail(dtoUser.getEmail().toLowerCase().trim());
		user.setPassword(hashedPassword);
		user.setFirstName(dtoUser.getFirstName().trim());
		user.setLastName(dtoUser.getLastName().trim());
		user.setPhone(dtoUser.getPhone() != null ? dtoUser.getPhone().trim() : null);
		user.setAddress(dtoUser.getAddress() != null ? dtoUser.getAddress().trim() : null);
		user.setCity(dtoUser.getCity() != null ? dtoUser.getCity().trim() : null);
		user.setPostalCode(dtoUser.getPostalCode() != null ? dtoUser.getPostalCode().trim() : null);
		user.setProvince(dtoUser.getProvince() != null ? dtoUser.getProvince().trim() : null);
		user.setCountry(dtoUser.getCountry() != null ? dtoUser.getCountry().trim() : null);
		user.setBirthDate(dtoUser.getBirthDate());
		user.setActive(dtoUser.isActive());
		user.setCreatedAt(new java.sql.Timestamp(dtoUser.getCreatedAt().getTime()));
		user.setUpdatedAt(new java.sql.Timestamp(dtoUser.getUpdatedAt().getTime()));

		// Guardar en base de datos
		userRepository.save(user);

		return "Usuario registrado correctamente";
	}

	/**
	 * Autentica a un usuario
	 * 
	 * @param email    - Email del usuario
	 * @param password - Contraseña del usuario
	 * @return DtoUser - Datos del usuario autenticado o null si falla
	 */
	public DtoUser login(String email, String password) {
		// Validaciones básicas
		if (email == null || email.trim().isEmpty()) {
			return null;
		}

		if (password == null || password.trim().isEmpty()) {
			return null;
		}

		// Buscar usuario por email
		Optional<User> userOptional = userRepository.findByEmail(email.toLowerCase().trim());

		if (!userOptional.isPresent()) {
			return null; // Usuario no encontrado
		}

		User user = userOptional.get();

		// Verificar si el usuario está activo
		if (!user.isActive()) {
			return null; // Usuario inactivo
		}

		// Verificar contraseña
		if (!passwordEncoder.matches(password, user.getPassword())) {
			return null; // Contraseña incorrecta
		}

		// Crear DtoUser con los datos (sin la contraseña)
		return convertToDto(user);
	}

	/**
	 * Obtiene todos los usuarios
	 * 
	 * @return List<DtoUser> - Lista de usuarios
	 */
	public List<DtoUser> findAll() {
		return userRepository.findAll().stream()
				.map(this::convertToDto)
				.collect(Collectors.toList());
	}

	/**
	 * Busca un usuario por ID
	 * 
	 * @param idUser - ID del usuario
	 * @return DtoUser - Usuario encontrado o null
	 */
	public DtoUser findById(String idUser) {
		Optional<User> userOptional = userRepository.findById(idUser);
		return userOptional.map(this::convertToDto).orElse(null);
	}

	/**
	 * Actualiza un usuario
	 * 
	 * @param idUser  - ID del usuario a actualizar
	 * @param dtoUser - Datos actualizados
	 * @return String - Mensaje de resultado
	 */
	public String update(String idUser, DtoUser dtoUser) {
		Optional<User> userOptional = userRepository.findById(idUser);

		if (!userOptional.isPresent()) {
			return "Usuario no encontrado";
		}

		User user = userOptional.get();

		// Validar email si cambió
		if (!user.getEmail().equals(dtoUser.getEmail())) {
			if (userRepository.existsByEmail(dtoUser.getEmail())) {
				return "El email ya está registrado";
			}
			user.setEmail(dtoUser.getEmail().toLowerCase().trim());
		}

		// Actualizar campos
		if (dtoUser.getFirstName() != null && !dtoUser.getFirstName().trim().isEmpty()) {
			user.setFirstName(dtoUser.getFirstName().trim());
		}

		if (dtoUser.getLastName() != null && !dtoUser.getLastName().trim().isEmpty()) {
			user.setLastName(dtoUser.getLastName().trim());
		}

		// Actualizar rol si se proporciona
		if (dtoUser.getIdRole() != null && !dtoUser.getIdRole().trim().isEmpty()) {
			Optional<Role> roleOptional = roleRepository.findById(dtoUser.getIdRole());
			if (!roleOptional.isPresent()) {
				return "El rol especificado no existe";
			}
			user.setRole(roleOptional.get());
		}

		// Actualizar campos opcionales
		user.setPhone(dtoUser.getPhone() != null ? dtoUser.getPhone().trim() : null);
		user.setAddress(dtoUser.getAddress() != null ? dtoUser.getAddress().trim() : null);
		user.setCity(dtoUser.getCity() != null ? dtoUser.getCity().trim() : null);
		user.setPostalCode(dtoUser.getPostalCode() != null ? dtoUser.getPostalCode().trim() : null);
		user.setProvince(dtoUser.getProvince() != null ? dtoUser.getProvince().trim() : null);
		user.setCountry(dtoUser.getCountry() != null ? dtoUser.getCountry().trim() : null);
		user.setBirthDate(dtoUser.getBirthDate());

		// Actualizar contraseña si se proporciona
		if (dtoUser.getPassword() != null && !dtoUser.getPassword().trim().isEmpty()) {
			if (dtoUser.getPassword().length() < 6) {
				return "La contraseña debe tener al menos 6 caracteres";
			}
			user.setPassword(passwordEncoder.encode(dtoUser.getPassword()));
		}

		user.setUpdatedAt(new Timestamp(new Date().getTime()));

		userRepository.save(user);

		return "Usuario actualizado correctamente";
	}

	/**
	 * Elimina (desactiva) un usuario
	 * 
	 * @param idUser - ID del usuario
	 * @return String - Mensaje de resultado
	 */
	public String delete(String idUser) {
		Optional<User> userOptional = userRepository.findById(idUser);

		if (!userOptional.isPresent()) {
			return "Usuario no encontrado";
		}

		User user = userOptional.get();
		user.setActive(false);
		user.setUpdatedAt(new Timestamp(new Date().getTime()));

		userRepository.save(user);

		return "Usuario eliminado correctamente";
	}

	/**
	 * Busca usuarios por rol
	 * 
	 * @param idRole - ID del rol
	 * @return List<DtoUser> - Lista de usuarios con ese rol
	 */
	public List<DtoUser> findByRole(String idRole) {
		return userRepository.findByRole_IdRole(idRole).stream()
				.map(this::convertToDto)
				.collect(Collectors.toList());
	}

	/**
	 * Convierte una entidad User a DtoUser
	 * 
	 * @param user - Entidad User
	 * @return DtoUser
	 */
	private DtoUser convertToDto(User user) {
		DtoUser dtoUser = new DtoUser();
		dtoUser.setIdUser(user.getIdUser());
		dtoUser.setIdRole(user.getRole().getIdRole());
		dtoUser.setEmail(user.getEmail());
		dtoUser.setFirstName(user.getFirstName());
		dtoUser.setLastName(user.getLastName());
		dtoUser.setPhone(user.getPhone());
		dtoUser.setAddress(user.getAddress());
		dtoUser.setCity(user.getCity());
		dtoUser.setPostalCode(user.getPostalCode());
		dtoUser.setProvince(user.getProvince());
		dtoUser.setCountry(user.getCountry());
		dtoUser.setBirthDate(user.getBirthDate());
		dtoUser.setActive(user.isActive());
		dtoUser.setCreatedAt(user.getCreatedAt());
		dtoUser.setUpdatedAt(user.getUpdatedAt());
		// NO se envía la contraseña por seguridad

		return dtoUser;
	}
}