package com.tpfinal.sportcenter_api.exception.user;

// El status (409) y el body los asigna GlobalExceptionHandler, que es la única
// fuente de verdad del manejo de errores. Por eso no lleva @ResponseStatus.
public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(String field, String value) {
        super("User with " + field + " '" + value + "' already exists.");
    }
}
