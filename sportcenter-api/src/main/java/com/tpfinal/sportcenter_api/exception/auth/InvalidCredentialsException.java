package com.tpfinal.sportcenter_api.exception.auth;

/**
 * Se lanza cuando el login falla, ya sea porque el email no existe o porque la
 * contraseña es incorrecta.
 *
 * <p>Es deliberadamente genérica: NO distingue entre "email inexistente" y
 * "contraseña incorrecta". Si diéramos errores distintos, un atacante podría
 * deducir qué emails están registrados (enumeración de usuarios). El status
 * (401) y el body uniforme los asigna
 * GlobalExceptionHandler.
 */
public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException() {
        super("Invalid email or password.");
    }
}
