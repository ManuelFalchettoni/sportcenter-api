package com.tpfinal.sportcenter_api.exception.user;

// El status (404) y el body los asigna GlobalExceptionHandler, que es la única
// fuente de verdad del manejo de errores. Por eso no lleva @ResponseStatus.
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Long id) {
        super("User not found. ID:" + id);
    }
}
