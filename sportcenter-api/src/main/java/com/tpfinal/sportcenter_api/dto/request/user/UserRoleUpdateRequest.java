package com.tpfinal.sportcenter_api.dto.request.user;

import com.tpfinal.sportcenter_api.enums.user.UserEnum;
import jakarta.validation.constraints.NotNull;

/**
 * DTO usado por el endpoint administrativo que cambia el rol de un usuario.
 * <p>
 * Está separado de {@code UserRequest} a propósito: el endpoint público de
 * creación/actualización nunca debe aceptar el rol desde el body. Cualquier
 * cambio de rol debe pasar por este DTO y por el endpoint protegido.
 */
public class UserRoleUpdateRequest {

    @NotNull
    private UserEnum role;

    public UserRoleUpdateRequest() {}

    public UserRoleUpdateRequest(UserEnum role) {
        this.role = role;
    }

    public UserEnum getRole() {
        return role;
    }

    public void setRole(UserEnum role) {
        this.role = role;
    }
}
