package com.tpfinal.sportcenter_api.controller.professional;

import com.tpfinal.sportcenter_api.dto.response.professional.ProfessionalAvailabilityResponse;
import com.tpfinal.sportcenter_api.service.professional.ProfessionalAvailabilityService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * Controlador REST que expone la agenda ocupada de un profesional en un día.
 * Ruta: GET /sportcenter/professionals/{id}/availability?date=YYYY-MM-DD.
 * Accesible a cualquier autenticado: un USER la necesita para elegir horario.
 */
@RestController
@RequestMapping("/sportcenter/professionals")
public class ProfessionalAvailabilityController {

    private final ProfessionalAvailabilityService professionalAvailabilityService;

    public ProfessionalAvailabilityController(ProfessionalAvailabilityService professionalAvailabilityService) {
        this.professionalAvailabilityService = professionalAvailabilityService;
    }

    /**
     * Lista los rangos horarios ocupados del profesional para el día pedido.
     * date es obligatorio (ISO yyyy-MM-dd); si falta o está mal formado,
     * Spring lanza su excepción estándar y el handler global responde 400.
     */
    @GetMapping("/{id}/availability")
    public ResponseEntity<ProfessionalAvailabilityResponse> findBusySlots(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(professionalAvailabilityService.findBusySlots(id, date));
    }
}
