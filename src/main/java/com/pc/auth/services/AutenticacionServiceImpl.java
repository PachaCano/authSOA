package com.pc.auth.services;

import com.pc.auth.dto.AuthResponse;
import com.pc.auth.dto.IniciarSesionRequest;
import com.pc.auth.dto.VerifTokenResponse;
import com.pc.auth.entities.AuthToken;
import com.pc.auth.entities.User;
import com.pc.auth.exceptions.AutenticacionException;
import com.pc.auth.repositories.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@AllArgsConstructor
@Slf4j
public class AutenticacionServiceImpl implements AutenticacionService {

    private final UserRepository userRepository;
    private final VerificationAuthToken verificationAuthToken;
    private final AuthenticationManager authenticationManager;

    @Transactional
    @Override
    public AuthResponse iniciarSesion(IniciarSesionRequest iniciarSesionRequest) {
        User usuario = this.userRepository.findFirstByUsername(iniciarSesionRequest.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Bad credentials"));

        Authentication authenticate = this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(iniciarSesionRequest.getUsername(),
                        iniciarSesionRequest.getPassword()));

        AuthToken aux = this.verificationAuthToken.generateAuthToken(authenticate);
        String token = aux.encodeToken();

        SecurityContextHolder.getContext().setAuthentication(authenticate);

        return AuthResponse.builder()
                .tokenEncript(token)
                .username(iniciarSesionRequest.getUsername())
                .rol(usuario.getRol().getNombre())
                .expiraEn(aux.getToDate())
                .build();
    }

    @Override
    public void cerrarSesion(String tokenEncript) {
        this.verificationAuthToken.delete(tokenEncript);
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    @Transactional
    @Override
    public AuthResponse refrescarToken(String tokenEncript) {
        AuthToken aux = this.verificationAuthToken.extendDateToken(tokenEncript);

        User user = this.userRepository.findFirstByUsername(aux.getUsername())
                .orElseThrow(() -> new AutenticacionException("No se encuentra el usuario en el sistema"));

        return AuthResponse.builder()
                .tokenEncript(aux.encodeToken())
                .username(aux.getUsername())
                .rol(user.getRol().getNombre())
                .expiraEn(aux.getToDate())
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public User getUsuarioActual() {
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return this.userRepository.findFirstByUsername(principal.getUsername())
                .orElseThrow(() -> new AutenticacionException("Usuario no encontrado: " +
                        principal.getUsername()));
    }

    @Override
    public VerifTokenResponse verificarToken(String tokenEncript) {
        AuthToken aux; User user;
        try {
            aux = this.verificationAuthToken.getAuthToken(tokenEncript);
            user = this.userRepository.findFirstByUsername(aux.getUsername()).get();
        } catch (AutenticacionException e) {
            return VerifTokenResponse.builder().mensaje(e.getMessage()).build();
        }
        if (aux.valid())
            return VerifTokenResponse.builder()
                    .username(aux.getUsername())
                    .tokenEncript(tokenEncript)
                    .rol(user.getRol().getNombre())
                    .mensaje("Token Valido")
                    .expiraEn(aux.getToDate()).build();

        return VerifTokenResponse.builder()
                .username(aux.getUsername())
                .tokenEncript(tokenEncript)
                .rol(user.getRol().getNombre())
                .mensaje("Token Invalido")
                .expiraEn(aux.getToDate()).build();
    }

    @Override
    public boolean estaLogueado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) throw new AutenticacionException("Usuario no logueado en el sistema o token vencido");
        return !(authentication instanceof AnonymousAuthenticationToken) && authentication.isAuthenticated();
    }

}
