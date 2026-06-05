package com.tpfinal.sportcenter_api.controller.user;

import com.tpfinal.sportcenter_api.dto.response.user.UserResponse;
import com.tpfinal.sportcenter_api.entity.user.User;
import com.tpfinal.sportcenter_api.service.user.UserFinderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sportcenter/users")
public class UserGetController {
    private final UserFinderService userFinderService;

    public UserGetController(UserFinderService userFinderService) {
        this.userFinderService = userFinderService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> find(@PathVariable Long id) {
        User user = userFinderService.find(id);
        UserResponse response = UserResponse.toResponse(user);
        return ResponseEntity.ok(response);
    }
}
