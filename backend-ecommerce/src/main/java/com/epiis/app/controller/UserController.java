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

import com.epiis.app.business.UserBusiness;
import com.epiis.app.controller.reqresp.user.RequestUserLogin;
import com.epiis.app.controller.reqresp.user.RequestUserRegister;
import com.epiis.app.controller.reqresp.user.RequestUserUpdate;
import com.epiis.app.controller.reqresp.user.ResponseUser;
import com.epiis.app.controller.reqresp.user.ResponseUserList;
import com.epiis.app.controller.reqresp.user.ResponseUserLogin;
import com.epiis.app.controller.reqresp.user.ResponseUserRegister;
import com.epiis.app.dto.DtoUser;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(path = "/user")
@Tag(name = "User", description = "API de gestión de usuarios")
public class UserController {

	@Autowired
	private UserBusiness userBusiness;

	@PostMapping(path = "/register", consumes = "multipart/form-data")
	@Operation(summary = "Registrar usuario", description = "Registra un nuevo usuario en el sistema")
	public ResponseEntity<ResponseUserRegister> register(@ModelAttribute RequestUserRegister request) {
		ResponseUserRegister response = new ResponseUserRegister();

		String result = this.userBusiness.register(request.getDto().getUser());

		if (result.equals("Usuario registrado correctamente")) {
			response.success();
			response.listMessage.add(result);
			response.setUser(request.getDto().getUser());
			return new ResponseEntity<>(response, HttpStatus.CREATED);
		} else {
			response.error();
			response.listMessage.add(result);
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping(path = "/login")
	@Operation(summary = "Iniciar sesión", description = "Autentica un usuario con email y contraseña")
	public ResponseEntity<ResponseUserLogin> login(@RequestBody RequestUserLogin request) {
		ResponseUserLogin response = new ResponseUserLogin();

		DtoUser user = this.userBusiness.login(
				request.getDto().getEmail(),
				request.getDto().getPassword());

		if (user != null) {
			response.success();
			response.listMessage.add("Login exitoso");
			response.setUser(user);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} else {
			response.error();
			response.listMessage.add("Credenciales incorrectas o usuario inactivo");
			return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
		}
	}

	@GetMapping(path = "/list")
	@Operation(summary = "Listar usuarios", description = "Obtiene todos los usuarios del sistema")
	public ResponseEntity<ResponseUserList> list() {
		ResponseUserList response = new ResponseUserList();

		List<DtoUser> users = this.userBusiness.findAll();

		response.success();
		response.setUsers(users);
		response.listMessage.add("Usuarios obtenidos correctamente");

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping(path = "/{id}")
	@Operation(summary = "Obtener usuario", description = "Obtiene un usuario por su ID")
	public ResponseEntity<ResponseUser> getById(@PathVariable("id") String id) {
		ResponseUser response = new ResponseUser();

		DtoUser user = this.userBusiness.findById(id);

		if (user != null) {
			response.success();
			response.setUser(user);
			response.listMessage.add("Usuario encontrado");
			return new ResponseEntity<>(response, HttpStatus.OK);
		} else {
			response.error();
			response.listMessage.add("Usuario no encontrado");
			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
		}
	}

	@PutMapping(path = "/update/{id}")
	@Operation(summary = "Actualizar usuario", description = "Actualiza un usuario existente")
	public ResponseEntity<ResponseUser> update(
			@PathVariable("id") String id,
			@RequestBody RequestUserUpdate request) {
		ResponseUser response = new ResponseUser();

		String result = this.userBusiness.update(id, request.getDto().getUser());

		if (result.equals("Usuario actualizado correctamente")) {
			response.success();
			response.listMessage.add(result);
			DtoUser updatedUser = this.userBusiness.findById(id);
			response.setUser(updatedUser);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} else {
			response.error();
			response.listMessage.add(result);
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}
	}

	@DeleteMapping(path = "/delete/{id}")
	@Operation(summary = "Eliminar usuario", description = "Elimina (desactiva) un usuario del sistema")
	public ResponseEntity<ResponseUser> delete(@PathVariable("id") String id) {
		ResponseUser response = new ResponseUser();

		String result = this.userBusiness.delete(id);

		if (result.equals("Usuario eliminado correctamente")) {
			response.success();
			response.listMessage.add(result);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} else {
			response.error();
			response.listMessage.add(result);
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping(path = "/by-role/{idRole}")
	@Operation(summary = "Usuarios por rol", description = "Obtiene todos los usuarios de un rol específico")
	public ResponseEntity<ResponseUserList> getByRole(@PathVariable("idRole") String idRole) {
		ResponseUserList response = new ResponseUserList();

		List<DtoUser> users = this.userBusiness.findByRole(idRole);

		response.success();
		response.setUsers(users);
		response.listMessage.add("Usuarios obtenidos correctamente");

		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}