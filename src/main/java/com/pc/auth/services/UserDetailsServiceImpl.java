package com.pc.auth.services;

import com.pc.auth.exceptions.AutenticacionException;
import com.pc.auth.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        return this.userRepository.findFirstByUsername(username)
                .orElseThrow(() -> new AutenticacionException("Usuario no encontrado con " +
                        "username: " + username));
    }

}
