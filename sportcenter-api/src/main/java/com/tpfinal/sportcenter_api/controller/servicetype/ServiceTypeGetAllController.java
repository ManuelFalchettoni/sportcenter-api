package com.tpfinal.sportcenter_api.controller.servicetype;

import com.tpfinal.sportcenter_api.dto.response.servicetype.ServiceTypeResponse;
import com.tpfinal.sportcenter_api.service.servicetype.ServiceTypeGetAllService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sportcenter/service-types")
public class ServiceTypeGetAllController {
    private final ServiceTypeGetAllService serviceTypeGetAllService;

    public ServiceTypeGetAllController(ServiceTypeGetAllService serviceTypeGetAllService) {
        this.serviceTypeGetAllService = serviceTypeGetAllService;
    }

    @GetMapping
    public ResponseEntity<Page<ServiceTypeResponse>> findAll(Pageable pageable) {
        Page<ServiceTypeResponse> response = serviceTypeGetAllService.findAll(pageable)
                .map(ServiceTypeResponse::toResponse);
        return ResponseEntity.ok(response);
    }
}
