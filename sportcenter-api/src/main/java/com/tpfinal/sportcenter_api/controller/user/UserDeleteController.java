package com.tpfinal.sportcenter_api.controller.user;

import com.tpfinal.sportcenter_api.service.user.UserDeleterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sportcenter/users")
public class UserDeleteController {
    private final UserDeleterService userDeleterService;

    public UserDeleteController(UserDeleterService userDeleterService) {
        this.userDeleterService = userDeleterService;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userDeleterService.delete(id);
        return ResponseEntity.noContent().build();

    }
}
