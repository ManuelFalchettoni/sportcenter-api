package com.tpfinal.sportcenter_api.dto.request.user;

import com.tpfinal.sportcenter_api.entity.user.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserRequest {

    @NotBlank
    @Size(min = 3, max = 30)
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$",
            message = "username may only contain letters, digits, dot, underscore or hyphen")
    private String username;

    @NotBlank
    @Email(regexp = "^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,}$")
    @Size(max = 254)
    private String email;

    // Sin @NotBlank a propósito: el PUT permite omitir el campo (null) para no cambiar la clave.
    // El POST valida la presencia explícitamente en UserCreatorService.
    // Max = 72 por el límite real de BCrypt (trunca silenciosamente más allá de 72 bytes).
    @Size(min = 8, max = 72)
    private String password;

    public UserRequest() {}

    public UserRequest(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static User fromRequest(UserRequest userRequest) {
        User user = new User();
        user.setUsername(userRequest.getUsername());
        user.setEmail(userRequest.getEmail());
        user.setPassword(userRequest.getPassword());
        return user;
    }
}
