package com.tpfinal.sportcenter_api.controller.servicetype;

import com.tpfinal.sportcenter_api.service.servicetype.ServiceTypeDeleterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sportcenter/service-type")
public class ServiceTypeDeleteController {
    private final ServiceTypeDeleterService serviceTypeDeleterService;

    public ServiceTypeDeleteController(ServiceTypeDeleterService serviceTypeDeleterService) {
        this.serviceTypeDeleterService = serviceTypeDeleterService;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        serviceTypeDeleterService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
