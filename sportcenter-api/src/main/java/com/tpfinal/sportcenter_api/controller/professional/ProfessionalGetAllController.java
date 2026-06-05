package com.tpfinal.sportcenter_api.controller.professional;

import com.tpfinal.sportcenter_api.dto.response.professional.ProfessionalResponse;
import com.tpfinal.sportcenter_api.service.professional.ProfessionalGetAllService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST que expone el listado paginado de profesionales.
 * Ruta base: {@code /sportcenter/professionals}.
 */
@RestController
@RequestMapping("/sportcenter/professionals")
public class ProfessionalGetAllController {
    private final ProfessionalGetAllService professionalGetAllService;

    public ProfessionalGetAllController(ProfessionalGetAllService professionalGetAllService) {
        this.professionalGetAllService = professionalGetAllService;
    }

    /**
     * Lista profesionales en forma paginada.
     *
     * @param pageable parámetros de paginación y orden.
     * @return 200 OK con la página de profesionales mapeados a DTO.
     */
    @GetMapping
    public ResponseEntity<Page<ProfessionalResponse>> findAll(Pageable pageable) {
        Page<ProfessionalResponse> response = professionalGetAllService.findAll(pageable)
                .map(ProfessionalResponse::toResponse);
        return ResponseEntity.ok(response);
    }
}
