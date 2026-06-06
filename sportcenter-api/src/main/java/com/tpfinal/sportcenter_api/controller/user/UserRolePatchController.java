package com.tpfinal.sportcenter_api.controller.user;

import com.tpfinal.sportcenter_api.dto.request.user.UserRoleUpdateRequest;
import com.tpfinal.sportcenter_api.dto.response.user.UserResponse;
import com.tpfinal.sportcenter_api.service.user.UserRoleUpdaterService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST administrativo para cambiar el rol de un usuario.
 * Ruta base: {@code /sportcenter/users}.
 *
 * <p><b>TODO (seguridad):</b> este endpoint debe quedar restringido a
 * usuarios con rol {@code ADMIN} cuando se integre JWT.
 * Sugerencia: anotar el método con {@code @PreAuthorize("hasRole('ADMIN')")}
 * y habilitar method security en {@code SecurityConfig}.
 */
@RestController
@RequestMapping("/sportcenter/users")
public class UserRolePatchController {

    private final UserRoleUpdaterService userRoleUpdaterService;

    public UserRolePatchController(UserRoleUpdaterService userRoleUpdaterService) {
        this.userRoleUpdaterService = userRoleUpdaterService;
    }

    /**
     * Actualiza el rol del usuario indicado.
     *
     * @param id identificador del usuario a modificar.
     * @param request body con el nuevo rol ({@code USER} o {@code ADMIN}).
     * @return 200 OK con el usuario actualizado.
     */
    @PatchMapping("/{id}/role")
    public ResponseEntity<UserResponse> updateRole(@PathVariable Long id,
                                                   @Valid @RequestBody UserRoleUpdateRequest request) {
        UserResponse response = userRoleUpdaterService.updateRole(id, request);
        return ResponseEntity.ok(response);
    }
}
