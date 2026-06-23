package com.tpfinal.sportcenter_api.controller.user;

import com.tpfinal.sportcenter_api.dto.response.user.UserResponse;
import com.tpfinal.sportcenter_api.service.user.UserGetAllService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST que expone el listado paginado de usuarios.
 * Ruta base: /sportcenter/users.
 */
@RestController
@RequestMapping("/sportcenter/users")
public class UserGetAllController {
    private final UserGetAllService userGetAllService;

    public UserGetAllController(UserGetAllService userGetAllService) {
        this.userGetAllService = userGetAllService;
    }

    /**
     * Lista usuarios en forma paginada. Parámetros de búsqueda opcionales
     * (case-insensitive, se combinan con AND):
     * - query: coincidencia sobre username o email.
     * - username: coincidencia sobre username.
     * - email: coincidencia sobre email.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<UserResponse>> findAll(
            Pageable pageable,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email) {
        Page<UserResponse> response = userGetAllService.findAll(pageable, query, username, email)
                .map(UserResponse::toResponse);
        return ResponseEntity.ok(response);
    }
}
