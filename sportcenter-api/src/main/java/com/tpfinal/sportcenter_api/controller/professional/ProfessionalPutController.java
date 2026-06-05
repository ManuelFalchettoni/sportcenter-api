package com.tpfinal.sportcenter_api.controller.professional;

import com.tpfinal.sportcenter_api.dto.request.professional.ProfessionalRequest;
import jakarta.validation.Valid;
import com.tpfinal.sportcenter_api.dto.response.professional.ProfessionalResponse;
import com.tpfinal.sportcenter_api.entity.professional.Professional;
import com.tpfinal.sportcenter_api.service.professional.ProfessionalUpdaterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/sportcenter/professionals")
public class ProfessionalPutController {
    private final ProfessionalUpdaterService professionalUpdaterService;


    public ProfessionalPutController(ProfessionalUpdaterService professionalUpdaterService) {
        this.professionalUpdaterService = professionalUpdaterService;
    }
    @PutMapping("/{id}")
    public ResponseEntity<ProfessionalResponse> update(@RequestBody @Valid ProfessionalRequest request, @PathVariable Long id){
        Professional professional = professionalUpdaterService.update(request, id);
        ProfessionalResponse response = ProfessionalResponse.toResponse(professional);
        return ResponseEntity.ok(response);
    }
}
