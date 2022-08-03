package com.pc.auth.security;

import com.pc.auth.entities.AuthToken;
import com.pc.auth.entities.User;
import com.pc.auth.services.VerificationAuthToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.Date;

@Component
@Slf4j
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final VerificationAuthToken verificationAuthToken;
    private final UserDetailsService userDetailsService;

    public TokenAuthenticationFilter(VerificationAuthToken verificationAuthToken, UserDetailsService userDetailsService) {
        super();
        this.verificationAuthToken = verificationAuthToken;
        this.userDetailsService = userDetailsService;
    }

    public static final String AUTH_PARAMETER = "xauthtoken";
    public static final String AUTH_PARAMETER1 = "token";
    public static final String AUTH_PARAMETER_AUTHORIZATION = "Authorization";

    private boolean esValido(String valor) {
        return valor != null && valor.trim().length() > 10;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws ServletException, IOException {

        String parameter = request.getParameter(AUTH_PARAMETER);
        if (!esValido(parameter))
            parameter = request.getParameter(AUTH_PARAMETER1);

        String header = request.getHeader(AUTH_PARAMETER_AUTHORIZATION);
        if (esValido(header) && header.toLowerCase().startsWith("bearer "))
            header = header.substring("Bearer ".length());

        if (!esValido(parameter) && !esValido(header)) {
            filterChain.doFilter(request, response);
            return;
        }
        String token;
        if (esValido(parameter))
            token = parameter;
        else
            token = header;

        String[] tokens;
        AuthToken authToken;

        try {
            tokens = AuthToken.decode(token);
            if (tokens.length != 2) {
                filterChain.doFilter(request, response);
                return;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            filterChain.doFilter(request, response);
            return;
        }

        // A partir de aquí, se considera que se envió el token, por
        // ende si no está ok, login inválido

        try {
            authToken = verificationAuthToken.getAuthToken(token);
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            log.debug("No existe el token=" + token);
            filterChain.doFilter(request, response);
            return;
        }

        if (!authToken.valid()) {
            SecurityContextHolder.clearContext();
            log.debug("El Token " + token + " ha expirado");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            authToken.setLastUsed(new Date());
            authToken.addRequest();
            verificationAuthToken.saveToken(authToken);
            log.warn(authToken.getUsername());
            String username = authToken.getUsername();
            User u = (User) userDetailsService.loadUserByUsername(username);
            log.trace("Token para usuario {} ({}) [{}]", u.getUsername(), token, request.getRequestURI());
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(u, null,
                    u.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            filterChain.doFilter(request, response);
        }

    }
}

