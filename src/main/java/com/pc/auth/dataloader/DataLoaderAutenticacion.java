package com.pc.auth.dataloader;

import com.pc.auth.entities.Rol;
import com.pc.auth.entities.User;
import com.pc.auth.exceptions.AutenticacionException;
import com.pc.auth.repositories.RolRepository;
import com.pc.auth.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@AllArgsConstructor
public class DataLoaderAutenticacion implements CommandLineRunner {

    private final RolRepository rolRepository;
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        if (this.rolRepository.findByNombre("ROLE_ADMIN").isEmpty()) {
            Rol admin = Rol.builder()
                    .nombre("ROLE_ADMIN")
                    .build();
            try {
                this.rolRepository.save(admin);
            } catch (DataIntegrityViolationException e) {
                e.getMessage();
            }

        }

        if (this.rolRepository.findByNombre("ROLE_USER").isEmpty()) {
            Rol user = Rol.builder()
                    .nombre("ROLE_USER")
                    .build();

            try {
                this.rolRepository.save(user);
            } catch (DataIntegrityViolationException e) {
                e.getMessage();
            }

        }

        if (this.userRepository.findByUsername("userAdmin").isEmpty()) {
            User admin = User.builder()
                    .nombre("Admin")
                    .apellido("PC")
                    .username("userAdmin")
                    .password(passwordEncoder.encode("elipacha2022"))
                    .enabled(true)
                    .fechaCreacion(new Date())
                    .rol(this.rolRepository.findByNombre("ROLE_ADMIN").orElse(null))
                    .build();

            try {
                this.userRepository.save(admin);
            } catch (DataIntegrityViolationException e) {
                throw new AutenticacionException(e.getMessage());
            }

        }

        if (this.userRepository.findByUsername("userUser").isEmpty()) {
            User user = User.builder()
                    .nombre("User")
                    .apellido("PC")
                    .username("userUser")
                    .password(passwordEncoder.encode("elipacha2022"))
                    .enabled(true)
                    .fechaCreacion(new Date())
                    .rol(this.rolRepository.findByNombre("ROLE_USER").orElse(null))
                    .build();

            try {
                this.userRepository.save(user);
            } catch (DataIntegrityViolationException e) {
                throw new AutenticacionException(e.getMessage());
            }

        }

    }
}
