package com.tpfinal.sportcenter_api.dto.request.user;

import com.tpfinal.sportcenter_api.Enum.user.UserEnum;
import com.tpfinal.sportcenter_api.entity.user.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class UserRequest {

    @NotBlank
    private String username;

    @NotBlank
    private String email;

    @NotBlank
    private String password;

    @NotNull
    private UserEnum role;

    @NotNull
    private LocalDateTime createdDate;

    public UserRequest(){}

    public UserRequest(String username, String email, String password, UserEnum role, LocalDateTime createdDate) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.createdDate = createdDate;
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

    public UserEnum getRole() {
        return role;
    }

    public void setRole(UserEnum role) {
        this.role = role;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public static User fromRequest(UserRequest userRequest){
        User user = new User();
        user.setUsername(userRequest.getUsername());
        user.setEmail(userRequest.getEmail());
        user.setPassword(userRequest.getPassword());
        user.setRole(userRequest.getRole());
        user.setCreatedDate(userRequest.getCreatedDate());
        return user;
    }


}
