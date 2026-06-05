package com.tpfinal.sportcenter_api.controller.user;

import com.tpfinal.sportcenter_api.service.user.UserDeleterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST que expone la baja de usuarios.
 * Ruta base: {@code /sportcenter/users}.
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
     *
     * @param id identificador del usuario.
     * @return 204 No Content si se eliminó correctamente.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userDeleterService.delete(id);
        return ResponseEntity.noContent().build();

    }
}
