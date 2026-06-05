package com.tpfinal.sportcenter_api.controller.professional;

import com.tpfinal.sportcenter_api.service.professional.ProfessionalDeleterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST que expone la baja de profesionales.
 * Ruta base: {@code /sportcenter/professionals}.
 */
@RestController
@RequestMapping("/sportcenter/professionals")
public class ProfessionalDeleteController {
    private final ProfessionalDeleterService professionalDeleterService;

    public ProfessionalDeleteController(ProfessionalDeleterService professionalDeleterService) {
        this.professionalDeleterService = professionalDeleterService;
    }
    /**
     * Elimina el profesional indicado.
     *
     * @param id identificador del profesional.
     * @return 204 No Content si se eliminó correctamente.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        professionalDeleterService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
