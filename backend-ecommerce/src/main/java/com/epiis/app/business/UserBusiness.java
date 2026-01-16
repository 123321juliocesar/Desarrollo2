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

import com.epiis.app.controller.reqresp.user.RequestUserRegister;
import com.epiis.app.dto.DtoUser;
import com.epiis.app.entity.Role;
import com.epiis.app.entity.User;
import com.epiis.app.repository.RoleRepository;
import com.epiis.app.repository.UserRepository;
import com.epiis.app.security.JwtService;

@Service
public class UserBusiness {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private JwtService jwtService;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * Registra un nuevo usuario
     * * @param dtoUser - Datos del usuario a registrar
     * @return String - Mensaje de resultado
     */
    public String register(RequestUserRegister request) {
        // 1. Validar que el request y el objeto interno no sean nulos
        if (request == null || request.getDto() == null || request.getDto().getUser() == null) {
        return "Error: Datos de registro no recibidos correctamente.";
        }

        DtoUser dtoUser = request.getDto().getUser();

        // 2. Validaciones básicas
        if (dtoUser.getEmail() == null || dtoUser.getEmail().trim().isEmpty()) {
            return "El email es obligatorio";
        }

        if (userRepository.existsByEmail(dtoUser.getEmail())) {
            return "El email ya está registrado";
        }

        if (dtoUser.getPassword() == null || dtoUser.getPassword().length() < 6) {
            return "La contraseña debe tener al menos 6 caracteres";
        }

        if (dtoUser.getIdRole() == null || dtoUser.getIdRole().trim().isEmpty()) {
            return "El rol es obligatorio";
        }

        // 3. Validar existencia del Rol
        Optional<Role> roleOptional = roleRepository.findById(dtoUser.getIdRole());
		if (!roleOptional.isPresent()) {
		return "El rol especificado no existe";
        }

        // 4. Preparar datos del DTO
        dtoUser.setIdUser(UUID.randomUUID().toString());
        dtoUser.setCreatedAt(new Date());
        dtoUser.setUpdatedAt(dtoUser.getCreatedAt());
        dtoUser.setActive(true);

        // 5. Mapear DTO a Entidad
        User user = new User();
        user.setIdUser(dtoUser.getIdUser());
        user.setRole(roleOptional.get());
        user.setEmail(dtoUser.getEmail().toLowerCase().trim());
        user.setPassword(passwordEncoder.encode(dtoUser.getPassword())); // Hashear
        user.setFirstName(dtoUser.getFirstName() != null ? dtoUser.getFirstName().trim() : "");
        user.setLastName(dtoUser.getLastName() != null ? dtoUser.getLastName().trim() : "");
        user.setPhone(dtoUser.getPhone());
        user.setAddress(dtoUser.getAddress());
        user.setBirthDate(dtoUser.getBirthDate());
        user.setActive(true);
        user.setCreatedAt(new java.sql.Timestamp(dtoUser.getCreatedAt().getTime()));
        user.setUpdatedAt(new java.sql.Timestamp(dtoUser.getUpdatedAt().getTime()));
        user.setRefreshToken(null);

        // 6. Guardar
        try {
            userRepository.save(user);
            return "Usuario registrado correctamente";
        } catch (Exception e) {
            return "Error al guardar en base de datos: " + e.getMessage();
        }
    }

    /**
     * Autentica a un usuario y gestiona la generación de tokens
     * * @param email    - Email del usuario
     * @param password - Contraseña del usuario
     * @return DtoUser - Datos del usuario con tokens incluidos
     */
    public DtoUser login(String email, String password) {
        // Validaciones básicas
        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return null;
        }

        // Buscar usuario por email
        Optional<User> userOptional = userRepository.findByEmail(email.toLowerCase().trim());

        if (!userOptional.isPresent()) {
            return null; 
        }

        User user = userOptional.get();

        // Verificar si el usuario está activo
        if (!user.isActive()) {
            return null; 
        }

        // Verificar contraseña
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return null; 
        }

        // --- LÓGICA DE TOKENS ---
        String accessToken = jwtService.generateToken(user.getEmail());
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());

        // Persistir el Refresh Token en BD para la rotación
        user.setRefreshToken(refreshToken);
        user.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        userRepository.save(user);

        // Convertir y asignar tokens al DTO
        DtoUser dtoUser = convertToDto(user);
        dtoUser.setAccessToken(accessToken);
        dtoUser.setRefreshToken(refreshToken);

        return dtoUser;
    }

    /**
     * Realiza la rotación de tokens (Token Rotation)
     * * @param oldRefreshToken - El refresh token actual del cliente
     * @return DtoUser - Nuevos tokens y datos de usuario, o null si es inválido
     */
    public DtoUser refreshToken(String oldRefreshToken) {
        try {
            // 1. Extraer username y validar expiración básica del token
            String email = jwtService.extractUsername(oldRefreshToken);
            Optional<User> userOptional = userRepository.findByEmail(email);

            if (userOptional.isPresent()) {
                User user = userOptional.get();

                // 2. ROTACIÓN: Verificar que el token enviado coincida con el de la BD
                // Si no coincide, puede ser un intento de reutilización (posible robo de token)
                if (user.getRefreshToken() != null && user.getRefreshToken().equals(oldRefreshToken)) {
                    
                    // 3. Validar integridad y expiración real del token
                    if (jwtService.validateToken(oldRefreshToken, user.getEmail())) {
                        
                        // 4. Generar nuevo par de tokens
                        String newAccessToken = jwtService.generateToken(user.getEmail());
                        String newRefreshToken = jwtService.generateRefreshToken(user.getEmail());

                        // 5. Actualizar BD con el NUEVO token (Invalida el anterior automáticamente)
                        user.setRefreshToken(newRefreshToken);
                        user.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
                        userRepository.save(user);

                        DtoUser dtoUser = convertToDto(user);
                        dtoUser.setAccessToken(newAccessToken);
                        dtoUser.setRefreshToken(newRefreshToken);
                        return dtoUser;
                    }
                } else {
                    // DETECCIÓN DE REUSO: Si el token es válido pero no coincide con la BD,
                    // significa que alguien ya usó ese token. Por seguridad, invalidamos todo.
                    user.setRefreshToken(null);
                    userRepository.save(user);
                }
            }
        } catch (Exception e) {
            // Token malformado o error de firma
            return null;
        }
        return null;
    }

    /**
     * Obtiene todos los usuarios
     * * @return List<DtoUser> - Lista de usuarios
     */
    public List<DtoUser> findAll() {
        return userRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Busca un usuario por ID
     * * @param idUser - ID del usuario
     * @return DtoUser - Usuario encontrado o null
     */
    public DtoUser findById(String idUser) {
        Optional<User> userOptional = userRepository.findById(idUser);
        return userOptional.map(this::convertToDto).orElse(null);
    }

    /**
     * Actualiza un usuario
     * * @param idUser  - ID del usuario a actualizar
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
        if (!user.getEmail().equalsIgnoreCase(dtoUser.getEmail().trim())) {
            if (userRepository.existsByEmail(dtoUser.getEmail())) {
                return "El email ya está registrado";
            }
            user.setEmail(dtoUser.getEmail().toLowerCase().trim());
        }

        // Actualizar campos obligatorios
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
        user.setPhone(dtoUser.getPhone() != null ? dtoUser.getPhone().trim() : user.getPhone());
        user.setAddress(dtoUser.getAddress() != null ? dtoUser.getAddress().trim() : user.getAddress());
        user.setCity(dtoUser.getCity() != null ? dtoUser.getCity().trim() : user.getCity());
        user.setPostalCode(dtoUser.getPostalCode() != null ? dtoUser.getPostalCode().trim() : user.getPostalCode());
        user.setProvince(dtoUser.getProvince() != null ? dtoUser.getProvince().trim() : user.getProvince());
        user.setCountry(dtoUser.getCountry() != null ? dtoUser.getCountry().trim() : user.getCountry());
        user.setBirthDate(dtoUser.getBirthDate() != null ? dtoUser.getBirthDate() : user.getBirthDate());

        // Actualizar contraseña si se proporciona
        if (dtoUser.getPassword() != null && !dtoUser.getPassword().trim().isEmpty()) {
            if (dtoUser.getPassword().length() < 6) {
                return "La contraseña debe tener al menos 6 caracteres";
            }
            user.setPassword(passwordEncoder.encode(dtoUser.getPassword()));
        }

        user.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

        userRepository.save(user);

        return "Usuario actualizado correctamente";
    }

    /**
     * Elimina (desactiva) un usuario
     * * @param idUser - ID del usuario
     * @return String - Mensaje de resultado
     */
    public String delete(String idUser) {
        Optional<User> userOptional = userRepository.findById(idUser);

        if (!userOptional.isPresent()) {
            return "Usuario no encontrado";
        }

        User user = userOptional.get();
        user.setActive(false);
        user.setRefreshToken(null); // Al eliminar, invalidamos su sesión actual
        user.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

        userRepository.save(user);

        return "Usuario eliminado correctamente";
    }

    /**
     * Busca usuarios por rol
     * * @param idRole - ID del rol
     * @return List<DtoUser> - Lista de usuarios con ese rol
     */
    public List<DtoUser> findByRole(String idRole) {
        return userRepository.findByRole_IdRole(idRole).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Convierte una entidad User a DtoUser
     * * @param user - Entidad User
     * @return DtoUser
     */
    private DtoUser convertToDto(User user) {
        DtoUser dtoUser = new DtoUser();
        dtoUser.setIdUser(user.getIdUser());
        
        if (user.getRole() != null) {
            dtoUser.setIdRole(user.getRole().getIdRole());
        }
        
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
        
        // El RefreshToken no se envía en findById o findAll, 
        // solo se asigna explícitamente en login o refresh.
        return dtoUser;
    }
}