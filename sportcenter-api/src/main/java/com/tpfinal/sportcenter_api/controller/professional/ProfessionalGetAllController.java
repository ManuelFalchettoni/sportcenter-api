package com.tpfinal.sportcenter_api.controller.professional;

import com.tpfinal.sportcenter_api.dto.response.professional.ProfessionalResponse;
import com.tpfinal.sportcenter_api.service.professional.ProfessionalGetAllService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST que expone el listado paginado de profesionales.
 * Ruta base: /sportcenter/professionals.
 */
@RestController
@RequestMapping("/sportcenter/professionals")
public class ProfessionalGetAllController {
    private final ProfessionalGetAllService professionalGetAllService;

    public ProfessionalGetAllController(ProfessionalGetAllService professionalGetAllService) {
        this.professionalGetAllService = professionalGetAllService;
    }

    /**
     * Lista profesionales en forma paginada. Parámetros de búsqueda opcionales
     * (case-insensitive, se combinan con AND):
     * - query: coincidencia sobre nombre o especialidad.
     * - name: coincidencia sobre nombre.
     * - speciality: coincidencia sobre especialidad.
     */
    @GetMapping
    public ResponseEntity<Page<ProfessionalResponse>> findAll(
            Pageable pageable,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String speciality) {
        Page<ProfessionalResponse> response = professionalGetAllService.findAll(pageable, query, name, speciality)
                .map(ProfessionalResponse::toResponse);
        return ResponseEntity.ok(response);
    }
}
