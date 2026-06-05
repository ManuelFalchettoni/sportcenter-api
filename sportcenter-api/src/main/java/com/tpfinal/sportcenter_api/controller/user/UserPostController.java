package com.tpfinal.sportcenter_api.controller.user;

import com.tpfinal.sportcenter_api.dto.request.user.UserRequest;
import com.tpfinal.sportcenter_api.dto.response.user.UserResponse;
import com.tpfinal.sportcenter_api.entity.user.User;
import com.tpfinal.sportcenter_api.service.user.UserCreatorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST que expone el alta de usuarios.
 * Ruta base: {@code /sportcenter/users}.
 */
@RestController
@RequestMapping("/sportcenter/users")
public class UserPostController {
    private final UserCreatorService userCreatorService;

    public UserPostController(UserCreatorService userCreatorService) {
        this.userCreatorService = userCreatorService;
    }

    /**
     * Registra un nuevo usuario.
     *
     * @param request datos validados del usuario a crear.
     * @return 201 Created con el usuario persistido en el cuerpo.
     */
    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserRequest request) {
        User user = userCreatorService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.toResponse(user));
    }
}
