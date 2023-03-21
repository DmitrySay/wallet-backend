package com.solbeg.wallet.security;

import org.springframework.security.authentication.AuthenticationServiceException;

public class JwtLoginException extends AuthenticationServiceException {
    public JwtLoginException(String msg) {
        super(msg);
    }
}
