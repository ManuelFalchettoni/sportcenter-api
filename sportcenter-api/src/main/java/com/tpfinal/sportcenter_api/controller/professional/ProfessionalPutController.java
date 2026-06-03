package com.tpfinal.sportcenter_api.controller.professional;

import com.tpfinal.sportcenter_api.dto.request.professional.ProfessionalRequest;
import com.tpfinal.sportcenter_api.dto.response.professional.ProfessionalResponse;
import com.tpfinal.sportcenter_api.entity.professional.Professional;
import com.tpfinal.sportcenter_api.service.professional.ProfessionalUpdaterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sportcenter/professional")
public class ProfessionalPutController {
    private final ProfessionalUpdaterService professionalUpdaterService;


    public ProfessionalPutController(ProfessionalUpdaterService professionalUpdaterService) {
        this.professionalUpdaterService = professionalUpdaterService;
    }
    @PutMapping("/{id}")
    public ResponseEntity<ProfessionalResponse> update(ProfessionalRequest request, Long id){
        Professional professional = professionalUpdaterService.update(request, id);
        ProfessionalResponse response = ProfessionalResponse.toResponse(professional);
        return ResponseEntity.ok(response);
    }
}
