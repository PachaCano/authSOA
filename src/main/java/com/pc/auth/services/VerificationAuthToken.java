package com.pc.auth.services;

import com.pc.auth.entities.AuthToken;
import com.pc.auth.entities.User;
import org.springframework.security.core.Authentication;

public interface VerificationAuthToken {

    AuthToken generateAuthToken(User usuario);

    AuthToken generateAuthToken(String username);

    AuthToken generateAuthToken(Authentication authenticate);

    AuthToken getAuthToken(String tokenEncript);

    void purgeTokens();

    void delete(String tokenEncript);

    void delete(AuthToken token);

    void saveToken(AuthToken token);

    boolean isValidDate(AuthToken tokenEncript);

    AuthToken extendDateToken(AuthToken token);

    AuthToken extendDateToken(String tokenEncript);
}
