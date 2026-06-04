package com.tpfinal.sportcenter_api.controller.servicetype;

import com.tpfinal.sportcenter_api.dto.response.servicetype.ServiceTypeResponse;
import com.tpfinal.sportcenter_api.service.servicetype.ServiceTypeFinderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sportcenter/service-type")
public class ServiceTypeGetController {
    private final ServiceTypeFinderService serviceTypeFinderService;

    public ServiceTypeGetController(ServiceTypeFinderService serviceTypeFinderService) {
        this.serviceTypeFinderService = serviceTypeFinderService;
    }
    @GetMapping("/{id}")
    public ResponseEntity<ServiceTypeResponse> find(@PathVariable Long id){
        ServiceTypeResponse response = ServiceTypeResponse.toResponse(serviceTypeFinderService.find(id));
        return ResponseEntity.ok(response);
    }
}
