package com.tpfinal.sportcenter_api.controller.user;

import com.tpfinal.sportcenter_api.dto.response.user.UserResponse;
import com.tpfinal.sportcenter_api.service.user.UserGetAllService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("sportcenter/users")
public class UserGetAllController {
    private final UserGetAllService userGetAllService;

    public UserGetAllController(UserGetAllService userGetAllService) {
        this.userGetAllService = userGetAllService;
    }

    @GetMapping
    public ResponseEntity<Page<UserResponse>> findAll(Pageable pageable) {
        Page<UserResponse> response = userGetAllService.findAll(pageable)
                .map(UserResponse::toResponse);
        return ResponseEntity.ok(response);
    }
}
