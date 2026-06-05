package com.tpfinal.sportcenter_api.controller.user;

import com.tpfinal.sportcenter_api.dto.request.user.UserRequest;
import com.tpfinal.sportcenter_api.dto.response.user.UserResponse;
import com.tpfinal.sportcenter_api.service.user.UserUpdaterService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("sportcenter/users")
public class UserPutController {
    private final UserUpdaterService userUpdaterService;

    public UserPutController(UserUpdaterService userUpdaterService) {
        this.userUpdaterService = userUpdaterService;
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(@PathVariable Long id,
                                               @Valid @RequestBody UserRequest request) {
        UserResponse response = userUpdaterService.update(id, request);
        return ResponseEntity.ok(response);
    }
}
