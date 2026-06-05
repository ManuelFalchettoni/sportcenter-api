package com.tpfinal.sportcenter_api.controller.professional;

import com.tpfinal.sportcenter_api.dto.request.professional.ProfessionalRequest;
import com.tpfinal.sportcenter_api.dto.response.professional.ProfessionalResponse;
import com.tpfinal.sportcenter_api.service.professional.ProfessionalCreatorService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/sportcenter/professionals")
public class ProfessionalPostController {
    private final ProfessionalCreatorService professionalCreatorService;

    public ProfessionalPostController(ProfessionalCreatorService professionalCreatorService) {
        this.professionalCreatorService = professionalCreatorService;
    }

    @PostMapping
    public ResponseEntity<ProfessionalResponse> create(@RequestBody @Valid ProfessionalRequest request){
        ProfessionalResponse response = ProfessionalResponse.toResponse(professionalCreatorService.create(request));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }
}
