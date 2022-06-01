package com.pc.auth.exceptions;

public class TokenException extends RuntimeException {
    public TokenException(String exMensaje) {
        super(exMensaje);
    }
}
