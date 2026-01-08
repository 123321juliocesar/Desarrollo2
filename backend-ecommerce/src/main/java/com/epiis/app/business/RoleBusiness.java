package com.epiis.app.business;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epiis.app.dto.DtoRole;
import com.epiis.app.entity.Role;
import com.epiis.app.repository.RoleRepository;

@Service
public class RoleBusiness {

    @Autowired
    private RoleRepository roleRepository;

    /**
     * Crea un nuevo rol
     * 
     * @param dtoRole - Datos del rol a crear
     * @return String - Mensaje de resultado
     */
    public String create(DtoRole dtoRole) {
        // Validación: Nombre no puede estar vacío
        if (dtoRole.getName() == null || dtoRole.getName().trim().isEmpty()) {
            return "El nombre del rol es obligatorio";
        }

        // Validación: Nombre debe ser único
        if (roleRepository.existsByName(dtoRole.getName().trim())) {
            return "Ya existe un rol con ese nombre";
        }

        // Generar ID y fechas
        dtoRole.setIdRole(UUID.randomUUID().toString());
        dtoRole.setCreatedAt(new Date());
        dtoRole.setUpdatedAt(dtoRole.getCreatedAt());

        // Crear entidad Role
        Role role = new Role();
        role.setIdRole(dtoRole.getIdRole());
        role.setName(dtoRole.getName().trim());
        role.setDescription(dtoRole.getDescription() != null ? dtoRole.getDescription().trim() : null);
        role.setCreatedAt(new java.sql.Timestamp(dtoRole.getCreatedAt().getTime()));
        role.setUpdatedAt(new java.sql.Timestamp(dtoRole.getUpdatedAt().getTime()));

        // Guardar en base de datos
        roleRepository.save(role);

        return "Rol creado correctamente";
    }

    /**
     * Obtiene todos los roles
     * 
     * @return List<DtoRole> - Lista de roles
     */
    public List<DtoRole> findAll() {
        return roleRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Busca un rol por ID
     * 
     * @param idRole - ID del rol
     * @return DtoRole - Rol encontrado o null
     */
    public DtoRole findById(String idRole) {
        Optional<Role> roleOptional = roleRepository.findById(idRole);
        return roleOptional.map(this::convertToDto).orElse(null);
    }

    /**
     * Actualiza un rol
     * 
     * @param idRole  - ID del rol a actualizar
     * @param dtoRole - Datos actualizados
     * @return String - Mensaje de resultado
     */
    public String update(String idRole, DtoRole dtoRole) {
        Optional<Role> roleOptional = roleRepository.findById(idRole);

        if (!roleOptional.isPresent()) {
            return "Rol no encontrado";
        }

        Role role = roleOptional.get();

        // Validar nombre si cambió
        if (!role.getName().equals(dtoRole.getName())) {
            if (roleRepository.existsByName(dtoRole.getName().trim())) {
                return "Ya existe un rol con ese nombre";
            }
            role.setName(dtoRole.getName().trim());
        }

        // Actualizar descripción
        role.setDescription(dtoRole.getDescription() != null ? dtoRole.getDescription().trim() : null);
        role.setUpdatedAt(new Timestamp(new Date().getTime()));

        roleRepository.save(role);

        return "Rol actualizado correctamente";
    }

    /**
     * Elimina un rol
     * 
     * @param idRole - ID del rol
     * @return String - Mensaje de resultado
     */
    public String delete(String idRole) {
        Optional<Role> roleOptional = roleRepository.findById(idRole);

        if (!roleOptional.isPresent()) {
            return "Rol no encontrado";
        }

        Role role = roleOptional.get();

        // Verificar si el rol tiene usuarios asignados
        if (role.getUsers() != null && !role.getUsers().isEmpty()) {
            return "No se puede eliminar el rol porque tiene usuarios asignados";
        }

        roleRepository.delete(role);

        return "Rol eliminado correctamente";
    }

    /**
     * Convierte una entidad Role a DtoRole
     * 
     * @param role - Entidad Role
     * @return DtoRole
     */
    private DtoRole convertToDto(Role role) {
        DtoRole dtoRole = new DtoRole();
        dtoRole.setIdRole(role.getIdRole());
        dtoRole.setName(role.getName());
        dtoRole.setDescription(role.getDescription());
        dtoRole.setCreatedAt(role.getCreatedAt());
        dtoRole.setUpdatedAt(role.getUpdatedAt());

        return dtoRole;
    }
}
