package com.tpfinal.sportcenter_api.controller.user;

import com.tpfinal.sportcenter_api.dto.request.user.UserRoleUpdateRequest;
import com.tpfinal.sportcenter_api.dto.response.user.UserResponse;
import com.tpfinal.sportcenter_api.service.user.UserRoleUpdaterService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST administrativo para cambiar el rol de un usuario.
 * Ruta base: /sportcenter/users.
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
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/role")
    public ResponseEntity<UserResponse> updateRole(@PathVariable Long id,
                                                   @Valid @RequestBody UserRoleUpdateRequest request) {
        UserResponse response = userRoleUpdaterService.updateRole(id, request);
        return ResponseEntity.ok(response);
    }
}
