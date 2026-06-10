package com.tpfinal.sportcenter_api.controller.user;

import com.tpfinal.sportcenter_api.config.UserPrincipal;
import com.tpfinal.sportcenter_api.dto.request.user.UserRequest;
import com.tpfinal.sportcenter_api.dto.response.user.UserResponse;
import com.tpfinal.sportcenter_api.service.user.UserUpdaterService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST que expone la actualización de usuarios.
 * Ruta base: /sportcenter/users.
 */
@RestController
@RequestMapping("/sportcenter/users")
public class UserPutController {
    private final UserUpdaterService userUpdaterService;

    public UserPutController(UserUpdaterService userUpdaterService) {
        this.userUpdaterService = userUpdaterService;
    }

    /**
     * Actualiza un usuario existente. Un admin puede editar a cualquiera;
     * un usuario común solo puede editarse a sí mismo (pantalla "Mi perfil").
     * El rol nunca se toca desde acá, y un no-ADMIN que cambia su contraseña
     * debe confirmar la actual (ver UserUpdaterService).
     */
    @PreAuthorize("hasRole('ADMIN') or #id == principal.id")
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(@PathVariable Long id,
                                               @Valid @RequestBody UserRequest request,
                                               @AuthenticationPrincipal UserPrincipal principal) {
        UserResponse response = userUpdaterService.update(id, request, principal.getUser());
        return ResponseEntity.ok(response);
    }
}
