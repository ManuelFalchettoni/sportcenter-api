package com.tpfinal.sportcenter_api.controller.professional;

import com.tpfinal.sportcenter_api.dto.request.professional.ProfessionalRequest;
import com.tpfinal.sportcenter_api.dto.response.professional.ProfessionalResponse;
import com.tpfinal.sportcenter_api.service.professional.ProfessionalCreatorService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

/**
 * Controlador REST que expone el alta de profesionales.
 * Ruta base: {@code /sportcenter/professionals}.
 */
@RestController
@RequestMapping("/sportcenter/professionals")
public class ProfessionalPostController {
    private final ProfessionalCreatorService professionalCreatorService;

    public ProfessionalPostController(ProfessionalCreatorService professionalCreatorService) {
        this.professionalCreatorService = professionalCreatorService;
    }

    /**
     * Crea un nuevo profesional.
     * <p>
     * La respuesta incluye el header {@code Location} apuntando al recurso creado.
     *
     * @param request datos validados del profesional.
     * @return 201 Created con el profesional persistido en el cuerpo.
     */
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
