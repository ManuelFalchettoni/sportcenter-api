package com.tpfinal.sportcenter_api.exception;

import com.tpfinal.sportcenter_api.exception.appointment.AppointmentAlreadyCancelledException;
import com.tpfinal.sportcenter_api.exception.appointment.AppointmentNotFoundException;
import com.tpfinal.sportcenter_api.exception.appointment.AppointmentNotPendingException;
import com.tpfinal.sportcenter_api.exception.appointment.AppointmentOverlapException;
import com.tpfinal.sportcenter_api.exception.appointment.UserAppointmentOverlapException;
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
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Manejador global de excepciones para todos los controladores REST.
 * Convierte excepciones conocidas en respuestas HTTP uniformes con un cuerpo
 * JSON que incluye timestamp, status, error y message.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Maneja errores de validación de @Valid en argumentos de controladores.
     * Construye un mapa {campo, mensaje} con cada FieldError detectado
     * y lo expone bajo la clave errors del cuerpo de respuesta.
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
     * lanzadas por la capa de servicio
     * ante datos inconsistentes (por ejemplo, rangos horarios inválidos).
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        Map<String, Object> body = baseBody(HttpStatus.BAD_REQUEST, ex.getMessage());
        return ResponseEntity.badRequest().body(body);
    }

    /**
     * Hibernate cuando la validación de Bean Validation falla en los callbacks pre-persist o
     * pre-update de una entidad
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
     * Un path variable o query param que no se puede convertir al tipo esperado
     * (ej: /users/abc donde se espera un Long, o date=10/07/2026 donde se espera
     * ISO yyyy-MM-dd) -> 400 Bad Request. Sin este handler caería en el
     * catch-all como 500, y el error es del cliente, no del servidor.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String expectedType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "expected type";
        Map<String, Object> body = baseBody(HttpStatus.BAD_REQUEST,
                "Invalid value for parameter '" + ex.getName() + "': expected a valid " + expectedType + ".");
        return ResponseEntity.badRequest().body(body);
    }

    /**
     * Recursos de dominio que no existen -> 404 Not Found.
     * Agrupamos las cuatro excepciones "not found" para que todas devuelvan el
     * mismo body uniforme.
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
     * Conflictos con el estado actual -> 409 Conflict: alta duplicada de usuario,
     * turno que se superpone con otro del mismo profesional o del mismo usuario,
     * intento de cancelar un turno ya cancelado, o de confirmar uno que no
     * está PENDING.
     */
    @ExceptionHandler({
            UserAlreadyExistsException.class,
            AppointmentOverlapException.class,
            UserAppointmentOverlapException.class,
            AppointmentAlreadyCancelledException.class,
            AppointmentNotPendingException.class
    })
    public ResponseEntity<Map<String, Object>> handleConflict(RuntimeException ex) {
        Map<String, Object> body = baseBody(HttpStatus.CONFLICT, ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    /**
     * Credenciales inválidas en el login -> 401 Unauthorized.
     * El mensaje es genérico a propósito (no dice si falló el email o la
     * contraseña) para no permitir enumerar usuarios registrados..
     */
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidCredentials(InvalidCredentialsException ex) {
        Map<String, Object> body = baseBody(HttpStatus.UNAUTHORIZED, ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    /**
     * Red de seguridad ante una violación de constraint de la base -> 409 Conflict.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrity(DataIntegrityViolationException ex) {
        log.warn("Violación de integridad de datos", ex);
        Map<String, Object> body = baseBody(HttpStatus.CONFLICT, "The request conflicts with an existing resource.");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    /**
     * Usuario autenticado pero sin permisos -> 403 Forbidden
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException ex) {
        Map<String, Object> body = baseBody(HttpStatus.FORBIDDEN, "Access denied.");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    /**
     * Catch-all para cualquier excepción no contemplada en los handlers de arriba
     * Si es una excepción propia de Spring MVC (ruta inexistente 404, metodo no
     * permitido 405, body JSON ilegible 400, etc.) ya trae su status correcto: lo respetamos en lugar de devolver 500.
     * Para tdo lo demás el detalle completo
     * en el servidor y deolvemos un 500 con mensaje genérico,
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
