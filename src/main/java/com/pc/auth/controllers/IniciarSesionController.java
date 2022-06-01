package com.pc.auth.controllers;

import com.pc.auth.dto.AuthResponse;
import com.pc.auth.dto.IniciarSesionRequest;
import com.pc.auth.dto.VerifTokenResponse;
import com.pc.auth.entities.User;
import com.pc.auth.exceptions.AutenticacionException;
import com.pc.auth.exceptions.TokenException;
import com.pc.auth.exceptions.UsuarioException;
import com.pc.auth.services.AutenticacionService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/auth", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class IniciarSesionController {

    private final AutenticacionService autenticacionService;

    @PostMapping("/login")
    public ResponseEntity<?> signin(@Valid @RequestBody IniciarSesionRequest iniciarSesionRequest, BindingResult result) {
        Map<String, Object> response = new HashMap<>();
        AuthResponse authResponse;

        if (result.hasErrors()) {
            List<String> errors = result.getFieldErrors()
                    .stream()
                    .map(err -> err.getField() + ": " + err.getDefaultMessage())
                    .collect(Collectors.toList());
            response.put("error", "Bad Request");
            response.put("errors", errors);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            authResponse = this.autenticacionService.iniciarSesion(iniciarSesionRequest);
        } catch (TokenException | AutenticacionException e) {
            response.put("mensaje", "Error al iniciar sesión");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }

    @PostMapping("/refresh/token")
    public ResponseEntity<?> refreshToken(@RequestParam("token") String tokenEncript) {
        Map<String, Object> response = new HashMap<>();
        AuthResponse authResponse;

        try {
            authResponse = this.autenticacionService.refrescarToken(tokenEncript);
        } catch (AutenticacionException e) {
            response.put("mensaje", "Error al refrescar token y extender sesión");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestParam("token") String tokenEncript) {
        Map<String, Object> response = new HashMap<>();
        try {
            this.autenticacionService.cerrarSesion(tokenEncript);
        } catch (AutenticacionException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", "Sesión cerrada con éxito");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/verify/token")
    public ResponseEntity<?> verificarToken(@RequestParam("token") String tokenEncript) {
        VerifTokenResponse response = this.autenticacionService.verificarToken(tokenEncript);
        if (response.getMensaje().equals("Token Valido"))
            return new ResponseEntity<>(response, HttpStatus.OK);
        else
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @GetMapping("/user")
    public ResponseEntity<?> obtenerUsuario() {
        Map<String, Object> response = new HashMap<>();
        User usuario;
        try {
            usuario = this.autenticacionService.getUsuarioActual();
        } catch (UsuarioException e) {
            response.put("mensaje", "Error al obtener los datos del cliente para el usuario logueado");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(usuario, HttpStatus.OK);
    }

}
