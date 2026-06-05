package com.tpfinal.sportcenter_api.controller.servicetype;

import com.tpfinal.sportcenter_api.service.servicetype.ServiceTypeDeleterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST que expone la baja de tipos de servicio.
 * Ruta base: {@code /sportcenter/service-types}.
 */
@RestController
@RequestMapping("/sportcenter/service-types")
public class ServiceTypeDeleteController {
    private final ServiceTypeDeleterService serviceTypeDeleterService;

    public ServiceTypeDeleteController(ServiceTypeDeleterService serviceTypeDeleterService) {
        this.serviceTypeDeleterService = serviceTypeDeleterService;
    }

    /**
     * Elimina el tipo de servicio indicado.
     *
     * @param id identificador del tipo de servicio.
     * @return 204 No Content si se eliminó correctamente.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        serviceTypeDeleterService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
