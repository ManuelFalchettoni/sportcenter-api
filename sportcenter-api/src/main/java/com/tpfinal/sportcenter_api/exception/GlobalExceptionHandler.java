package com.tpfinal.sportcenter_api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Manejador global de excepciones para todos los controladores REST.
 * <p>
 * Convierte excepciones conocidas en respuestas HTTP uniformes con un cuerpo
 * JSON que incluye timestamp, status, error y message.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja errores de validación de {@code @Valid} en argumentos de controladores.
     * <p>
     * Construye un mapa {campo, mensaje} con cada {@link FieldError} detectado
     * y lo expone bajo la clave {@code errors} del cuerpo de respuesta.
     *
     * @param ex excepción lanzada por Spring al fallar las validaciones del body.
     * @return 400 Bad Request con el detalle de los campos inválidos.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }
        Map<String, Object> body = baseBody(HttpStatus.BAD_REQUEST, "Validation failed");
        body.put("errors", fieldErrors);
        return ResponseEntity.badRequest().body(body);
    }

    /**
     * Maneja {@link IllegalArgumentException} lanzadas por la capa de servicio
     * ante datos inconsistentes (por ejemplo, rangos horarios inválidos).
     *
     * @param ex excepción capturada.
     * @return 400 Bad Request con el mensaje original como {@code message}.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        Map<String, Object> body = baseBody(HttpStatus.BAD_REQUEST, ex.getMessage());
        return ResponseEntity.badRequest().body(body);
    }

    /**
     * Construye el cuerpo base común a todas las respuestas de error.
     *
     * @param status estado HTTP a devolver.
     * @param message mensaje descriptivo del error.
     * @return mapa mutable con las claves {@code timestamp}, {@code status},
     *         {@code error} y {@code message}.
     */
    private Map<String, Object> baseBody(HttpStatus status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        return body;
    }
}
