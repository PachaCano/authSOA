package com.pc.auth.services;

import com.pc.auth.dto.AuthResponse;
import com.pc.auth.dto.IniciarSesionRequest;
import com.pc.auth.dto.VerifTokenResponse;
import com.pc.auth.entities.User;

public interface AutenticacionService {

    AuthResponse iniciarSesion(IniciarSesionRequest iniciarSesionRequest);

    void cerrarSesion(String tokenEncript);

    AuthResponse refrescarToken(String tokenEncript);

    User getUsuarioActual();

    VerifTokenResponse verificarToken(String tokenEncript);

    boolean estaLogueado();
}
