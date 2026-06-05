package com.tpfinal.sportcenter_api.controller.professional;

import com.tpfinal.sportcenter_api.dto.response.professional.ProfessionalResponse;
import com.tpfinal.sportcenter_api.service.professional.ProfessionalFinderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sportcenter/professionals")
public class ProfessionalGetController {
    private final ProfessionalFinderService professionalFinderService;

    public ProfessionalGetController(ProfessionalFinderService professionalFinderService) {
        this.professionalFinderService = professionalFinderService;
    }
    @GetMapping("/{id}")
    public ResponseEntity<ProfessionalResponse> find(@PathVariable Long id){
        ProfessionalResponse response = ProfessionalResponse.toResponse(professionalFinderService.find(id));
        return ResponseEntity.ok(response);
    }
}
