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

@RestController
@RequestMapping("sportcenter/users")
public class UserPostController {
    private final UserCreatorService userCreatorService;

    public UserPostController(UserCreatorService userCreatorService) {
        this.userCreatorService = userCreatorService;
    }

    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserRequest request) {
        User user = userCreatorService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.toResponse(user));
    }
}
