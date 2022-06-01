package com.pc.auth.services;

import com.pc.auth.entities.User;
import com.pc.auth.exceptions.AutenticacionException;
import com.pc.auth.exceptions.UsuarioException;
import com.pc.auth.repositories.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AutenticacionService autenticacionService;

    @Override
    public User obtenerDatosUser() {
        if (!this.autenticacionService.estaLogueado())
            throw new AutenticacionException("Usuario no logueado en el sistema");

        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return this.userRepository.findFirstByUsername(principal.getUsername())
                .orElseThrow(() -> new AutenticacionException("Usuario no encontrado: " +
                        principal.getUsername()));
    }


    @Override
    public User findByUsername(String userEmail) {
        return this.userRepository.findFirstByUsername(userEmail)
                .orElseThrow(() -> new UsuarioException("El usuario con email = " + userEmail + " no existe."));
    }

    @Override
    public Optional<User> findByUsernameOpt(String userEmail) {
        return this.userRepository.findFirstByUsername(userEmail);
    }

    @Override
    public List<User> findAll() {
        return null;
    }

    @Override
    public User findById(Long aLong) {
        return null;
    }

    @Override
    public User save(User object) {
        return this.userRepository.save(object);
    }

    @Override
    public void delete(User object) {

    }

    @Override
    public void deleteById(Long aLong) {

    }
}
