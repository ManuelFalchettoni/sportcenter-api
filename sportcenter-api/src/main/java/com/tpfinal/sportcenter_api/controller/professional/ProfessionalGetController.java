package com.tpfinal.sportcenter_api.controller.professional;

import com.tpfinal.sportcenter_api.dto.response.professional.ProfessionalResponse;
import com.tpfinal.sportcenter_api.service.professional.ProfessionalFinderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST que expone la consulta de un profesional por ID.
 * Ruta base: {@code /sportcenter/professionals}.
 */
@RestController
@RequestMapping("/sportcenter/professionals")
public class ProfessionalGetController {
    private final ProfessionalFinderService professionalFinderService;

    public ProfessionalGetController(ProfessionalFinderService professionalFinderService) {
        this.professionalFinderService = professionalFinderService;
    }
    /**
     * Obtiene un profesional por su ID.
     *
     * @param id identificador del profesional.
     * @return 200 OK con el profesional encontrado.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProfessionalResponse> find(@PathVariable Long id){
        ProfessionalResponse response = ProfessionalResponse.toResponse(professionalFinderService.find(id));
        return ResponseEntity.ok(response);
    }
}
