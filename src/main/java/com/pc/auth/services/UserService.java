package com.pc.auth.services;

import com.pc.auth.entities.User;
import com.pc.auth.util.CrudService;

import java.util.Optional;

public interface UserService extends CrudService<User, Long> {

    User obtenerDatosUser();

    User findByUsername (String username);

    Optional<User> findByUsernameOpt (String username);

}
