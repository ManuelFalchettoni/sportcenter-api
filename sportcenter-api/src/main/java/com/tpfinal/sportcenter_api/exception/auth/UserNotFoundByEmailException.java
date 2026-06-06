package com.tpfinal.sportcenter_api.exception.auth;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundByEmailException extends RuntimeException {

    public UserNotFoundByEmailException(String email) {
        super("User not found. email: " + email);
    }
}
