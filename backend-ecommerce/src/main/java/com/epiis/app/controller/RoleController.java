package com.epiis.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epiis.app.business.RoleBusiness;
import com.epiis.app.controller.reqresp.role.RequestRoleCreate;
import com.epiis.app.controller.reqresp.role.RequestRoleUpdate;
import com.epiis.app.controller.reqresp.role.ResponseRole;
import com.epiis.app.controller.reqresp.role.ResponseRoleList;
import com.epiis.app.dto.DtoRole;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(path = "/role")
@Tag(name = "Role", description = "API de gestión de roles")
public class RoleController {

    @Autowired
    private RoleBusiness roleBusiness;

    @PostMapping(path = "/create", consumes = "multipart/form-data")
    @Operation(summary = "Crear rol", description = "Crea un nuevo rol en el sistema")
    public ResponseEntity<ResponseRole> create(@ModelAttribute RequestRoleCreate request) {
        ResponseRole response = new ResponseRole();

        String result = this.roleBusiness.create(request.getDto().getRole());

        if (result.equals("Rol creado correctamente")) {
            response.success();
            response.listMessage.add(result);
            response.setRole(request.getDto().getRole());
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else {
            response.error();
            response.listMessage.add(result);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(path = "/list")
    @Operation(summary = "Listar roles", description = "Obtiene todos los roles del sistema")
    public ResponseEntity<ResponseRoleList> list() {
        ResponseRoleList response = new ResponseRoleList();

        List<DtoRole> roles = this.roleBusiness.findAll();

        response.success();
        response.setRoles(roles);
        response.listMessage.add("Roles obtenidos correctamente");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(path = "/{id}")
    @Operation(summary = "Obtener rol", description = "Obtiene un rol por su ID")
    public ResponseEntity<ResponseRole> getById(@PathVariable("id") String id) {
        ResponseRole response = new ResponseRole();

        DtoRole role = this.roleBusiness.findById(id);

        if (role != null) {
            response.success();
            response.setRole(role);
            response.listMessage.add("Rol encontrado");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.error();
            response.listMessage.add("Rol no encontrado");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping(path = "/update/{id}")
    @Operation(summary = "Actualizar rol", description = "Actualiza un rol existente")
    public ResponseEntity<ResponseRole> update(
            @PathVariable("id") String id,
            @RequestBody RequestRoleUpdate request) {
        ResponseRole response = new ResponseRole();

        String result = this.roleBusiness.update(id, request.getDto().getRole());

        if (result.equals("Rol actualizado correctamente")) {
            response.success();
            response.listMessage.add(result);
            DtoRole updatedRole = this.roleBusiness.findById(id);
            response.setRole(updatedRole);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.error();
            response.listMessage.add(result);
            response.setRole(request.getDto().getRole());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping(path = "/delete/{id}")
    @Operation(summary = "Eliminar rol", description = "Elimina un rol del sistema")
    public ResponseEntity<ResponseRole> delete(@PathVariable("id") String id) {
        ResponseRole response = new ResponseRole();

        String result = this.roleBusiness.delete(id);

        if (result.equals("Rol eliminado correctamente")) {
            response.success();
            response.listMessage.add(result);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.error();
            response.listMessage.add(result);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
}
