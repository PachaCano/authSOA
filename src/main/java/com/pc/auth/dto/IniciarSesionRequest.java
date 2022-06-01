package com.pc.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IniciarSesionRequest {

    @NotNull(message = "Ingresa tu username")
    private String username;

    @NotNull(message = "Ingresa tu contraseña")
    private String password;
}
