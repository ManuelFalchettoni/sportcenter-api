package com.tpfinal.sportcenter_api.controller.servicetype;

import com.tpfinal.sportcenter_api.service.servicetype.ServiceTypeDeleterService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST que expone la baja de tipos de servicio.
 * Ruta base: /sportcenter/service-types.
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
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        serviceTypeDeleterService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
