package com.tpfinal.sportcenter_api.controller.user;

import com.tpfinal.sportcenter_api.service.user.UserDeleterService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST que expone la baja de usuarios.
 * Ruta base: /sportcenter/users.
 */
@RestController
@RequestMapping("/sportcenter/users")
public class UserDeleteController {
    private final UserDeleterService userDeleterService;

    public UserDeleteController(UserDeleterService userDeleterService) {
        this.userDeleterService = userDeleterService;
    }

    /**
     * Elimina el usuario indicado.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userDeleterService.delete(id);
        return ResponseEntity.noContent().build();

    }
}
