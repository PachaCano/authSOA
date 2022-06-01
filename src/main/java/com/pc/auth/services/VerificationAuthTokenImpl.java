package com.pc.auth.services;

import com.pc.auth.entities.AuthToken;
import com.pc.auth.entities.User;
import com.pc.auth.exceptions.AutenticacionException;
import com.pc.auth.repositories.AuthTokenRepository;
import com.pc.auth.repositories.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Calendar;
import java.util.Date;

@Service
@AllArgsConstructor
@Slf4j
public class VerificationAuthTokenImpl implements VerificationAuthToken {

    private final AuthTokenRepository authTokenRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public AuthToken generateAuthToken(User usuario) {
        return this.authTokenRepository.save(new AuthToken(usuario, this.fechaExpiracion()));
    }

    @Transactional
    @Override
    public AuthToken generateAuthToken(String username) {
        return this.authTokenRepository.save(new AuthToken(this.userRepository.findFirstByUsername(username).get(), this.fechaExpiracion()));
    }

    @Transactional
    @Override
    public AuthToken generateAuthToken(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        AuthToken aux = this.authTokenRepository.findFirstByUsername(user.getUsername()).orElse(null);
        if (aux != null) {
            aux.addRequest();
            this.extendDateToken(aux);
            return aux;
        }
        return this.authTokenRepository.save(new AuthToken(user, this.fechaExpiracion()));
    }

    @Override
    @Transactional
    public AuthToken getAuthToken(String tokenEncript) {
        return this.authTokenRepository.findById(AuthToken.decode(tokenEncript)[0])
                .orElseThrow(() -> new AutenticacionException("Token inválido"));
    }

    @Override
    @Transactional
    @Scheduled(cron = "0 0 * * * *")    // A cada hora
    public void purgeTokens() {
        log.info("Scheduled de tokens"); this.authTokenRepository.purgeToDate(new Date());
    }

    @Override
    public void delete(String tokenEncript) {
        String part = AuthToken.decode(tokenEncript)[0];
        this.authTokenRepository.findById(part)
                .orElseThrow(() -> new AutenticacionException("No existe el token"));
        this.authTokenRepository.deleteById(part);
    }

    @Override
    public void delete(AuthToken token) {
        this.authTokenRepository.deleteById(token.getSeries());
    }

    @Override
    @Transactional
    public void saveToken(AuthToken token) {
        this.authTokenRepository.save(token);
    }

    @Override
    public boolean isValidDate(AuthToken token) {
        return token.valid();
    }

    @Override
    public AuthToken extendDateToken(AuthToken token) {
        token.setToDate(this.fechaExpiracion());
        return this.authTokenRepository.save(token);
    }

    @Override
    public AuthToken extendDateToken(String tokenEncript) {
        AuthToken aux = this.authTokenRepository.findById(AuthToken.decode(tokenEncript)[0])
                .orElseThrow(() -> new AutenticacionException("No existe el token en la base de datos"));
        return this.extendDateToken(aux);
    }

    private Date fechaExpiracion() {
        Calendar calendar = Calendar.getInstance();
        Date date = new Date();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR, 24);
        date = calendar.getTime();
        return date;
    }
}
