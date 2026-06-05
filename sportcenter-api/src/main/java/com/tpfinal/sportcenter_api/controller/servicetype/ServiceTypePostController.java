package com.tpfinal.sportcenter_api.controller.servicetype;

import com.tpfinal.sportcenter_api.dto.request.servicetype.ServiceTypeRequest;
import com.tpfinal.sportcenter_api.dto.response.servicetype.ServiceTypeResponse;
import com.tpfinal.sportcenter_api.service.servicetype.ServiceTypeCreatorService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/sportcenter/service-types")
public class ServiceTypePostController {
    private final ServiceTypeCreatorService serviceTypeCreatorService;

    public ServiceTypePostController(ServiceTypeCreatorService serviceTypeCreatorService) {
        this.serviceTypeCreatorService = serviceTypeCreatorService;
    }

    @PostMapping
    public ResponseEntity<ServiceTypeResponse> create(@RequestBody @Valid ServiceTypeRequest request){
        ServiceTypeResponse response = ServiceTypeResponse.toResponse(serviceTypeCreatorService.create(request));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }
}
