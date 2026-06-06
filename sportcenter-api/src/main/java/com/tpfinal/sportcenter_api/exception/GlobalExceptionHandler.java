package com.tpfinal.sportcenter_api.exception;

import com.tpfinal.sportcenter_api.exception.appointment.AppointmentNotFoundException;
import com.tpfinal.sportcenter_api.exception.appointment.AppointmentOverlapException;
import com.tpfinal.sportcenter_api.exception.auth.InvalidCredentialsException;
import com.tpfinal.sportcenter_api.exception.professional.ProfessionalNotFoundException;
import com.tpfinal.sportcenter_api.exception.servicetype.ServiceTypeNotFoundException;
import com.tpfinal.sportcenter_api.exception.user.UserAlreadyExistsException;
import com.tpfinal.sportcenter_api.exception.user.UserNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponse;
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
 * JSON que incluye timestamp, status, error y message. Es la única fuente de
 * verdad del status y del formato de error: por eso las excepciones de dominio
 * NO usan {@code @ResponseStatus} (si lo hicieran, el status quedaría definido
 * en dos lugares y podrían divergir).
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

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
     * Maneja {@link ConstraintViolationException} lanzadas por Hibernate cuando
     * la validación de Bean Validation falla en los callbacks pre-persist o
     * pre-update de una entidad (datos inválidos llegando por una vía que no
     * pasó por {@code @Valid} en un controller).
     *
     * @param ex excepción capturada.
     * @return 400 Bad Request con un mapa {propiedad, mensaje} bajo {@code errors}.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            fieldErrors.put(violation.getPropertyPath().toString(), violation.getMessage());
        }
        Map<String, Object> body = baseBody(HttpStatus.BAD_REQUEST, "Entity validation failed");
        body.put("errors", fieldErrors);
        return ResponseEntity.badRequest().body(body);
    }

    /**
     * Recursos de dominio que no existen -> 404 Not Found.
     * <p>
     * Agrupamos las cuatro excepciones "not found" para que todas devuelvan el
     * mismo body uniforme. El mensaje lo definimos nosotros, así que es seguro
     * exponerlo al cliente.
     *
     * @param ex la excepción de "no encontrado" capturada.
     * @return 404 Not Found con el mensaje original como {@code message}.
     */
    @ExceptionHandler({
            UserNotFoundException.class,
            ProfessionalNotFoundException.class,
            ServiceTypeNotFoundException.class,
            AppointmentNotFoundException.class
    })
    public ResponseEntity<Map<String, Object>> handleNotFound(RuntimeException ex) {
        Map<String, Object> body = baseBody(HttpStatus.NOT_FOUND, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    /**
     * Conflictos con el estado actual -> 409 Conflict: alta duplicada de usuario
     * o turno que se superpone con otro del mismo profesional.
     *
     * @param ex la excepción de conflicto capturada.
     * @return 409 Conflict con el mensaje original como {@code message}.
     */
    @ExceptionHandler({
            UserAlreadyExistsException.class,
            AppointmentOverlapException.class
    })
    public ResponseEntity<Map<String, Object>> handleConflict(RuntimeException ex) {
        Map<String, Object> body = baseBody(HttpStatus.CONFLICT, ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    /**
     * Credenciales inválidas en el login -> 401 Unauthorized.
     * <p>
     * El mensaje es genérico a propósito (no dice si falló el email o la
     * contraseña) para no permitir enumerar usuarios registrados.
     *
     * @param ex excepción capturada.
     * @return 401 Unauthorized con el mensaje genérico.
     */
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidCredentials(InvalidCredentialsException ex) {
        Map<String, Object> body = baseBody(HttpStatus.UNAUTHORIZED, ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    /**
     * Red de seguridad ante una violación de constraint de la base -> 409 Conflict.
     * <p>
     * Cubre, por ejemplo, una carrera en el alta de usuarios que esquive el
     * chequeo previo de unicidad y choque contra el índice UNIQUE. Usamos un
     * mensaje genérico porque {@code ex.getMessage()} expone detalles internos
     * de SQL que no queremos filtrar al cliente.
     *
     * @param ex excepción capturada.
     * @return 409 Conflict con un mensaje genérico.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrity(DataIntegrityViolationException ex) {
        log.warn("Violación de integridad de datos", ex);
        Map<String, Object> body = baseBody(HttpStatus.CONFLICT, "The request conflicts with an existing resource.");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    /**
     * Usuario autenticado pero sin permisos -> 403 Forbidden (por ejemplo, un
     * USER intentando un endpoint anotado con {@code @PreAuthorize("hasRole('ADMIN')")}).
     * <p>
     * Lo manejamos explícitamente para darle el body uniforme y, sobre todo, para
     * que el catch-all de abajo no lo confunda con un error inesperado (500):
     * {@link AccessDeniedException} no implementa {@link ErrorResponse}, así que
     * sin este handler caería en el 500 genérico.
     *
     * @param ex excepción capturada.
     * @return 403 Forbidden con un mensaje genérico.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException ex) {
        Map<String, Object> body = baseBody(HttpStatus.FORBIDDEN, "Access denied.");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    /**
     * Catch-all para cualquier excepción no contemplada en los handlers de arriba.
     * <p>
     * Si es una excepción propia de Spring MVC (ruta inexistente 404, metodo no
     * permitido 405, body JSON ilegible 400, etc.) implementa {@link ErrorResponse}
     * y ya trae su status correcto: lo respetamos en lugar de devolver 500. Para
     * tdo lo demás (errores realmente inesperados) logueamos el detalle completo
     * en el servidor y devolvemos un 500 con mensaje genérico, sin filtrar
     * internals (stack traces, mensajes de librerías, etc.) al cliente.
     *
     * @param ex excepción capturada.
     * @return el status propio de la excepción MVC, o 500 para lo inesperado.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleUnexpected(Exception ex) {
        if (ex instanceof ErrorResponse errorResponse) {
            HttpStatus status = HttpStatus.valueOf(errorResponse.getStatusCode().value());
            Map<String, Object> body = baseBody(status, status.getReasonPhrase());
            return ResponseEntity.status(status).body(body);
        }
        log.error("Error inesperado procesando la petición", ex);
        Map<String, Object> body = baseBody(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
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
