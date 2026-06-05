package com.tpfinal.sportcenter_api.controller.user;

import com.tpfinal.sportcenter_api.dto.request.user.UserRequest;
import com.tpfinal.sportcenter_api.dto.response.user.UserResponse;
import com.tpfinal.sportcenter_api.service.user.UserUpdaterService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST que expone la actualización de usuarios.
 * Ruta base: {@code /sportcenter/users}.
 */
@RestController
@RequestMapping("/sportcenter/users")
public class UserPutController {
    private final UserUpdaterService userUpdaterService;

    public UserPutController(UserUpdaterService userUpdaterService) {
        this.userUpdaterService = userUpdaterService;
    }

    /**
     * Actualiza un usuario existente.
     *
     * @param id identificador del usuario a actualizar.
     * @param request datos validados con el nuevo estado del usuario.
     * @return 200 OK con el usuario actualizado.
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(@PathVariable Long id,
                                               @Valid @RequestBody UserRequest request) {
        UserResponse response = userUpdaterService.update(id, request);
        return ResponseEntity.ok(response);
    }
}
