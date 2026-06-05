package com.tpfinal.sportcenter_api.controller.servicetype;

import com.tpfinal.sportcenter_api.dto.request.servicetype.ServiceTypeRequest;
import com.tpfinal.sportcenter_api.dto.response.servicetype.ServiceTypeResponse;
import com.tpfinal.sportcenter_api.entity.servicetype.ServiceType;
import com.tpfinal.sportcenter_api.service.servicetype.ServiceTypeUpdaterService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST que expone la actualización de tipos de servicio.
 * Ruta base: {@code /sportcenter/service-types}.
 */
@RestController
@RequestMapping("/sportcenter/service-types")
public class ServiceTypePutController {
    private final ServiceTypeUpdaterService serviceTypeUpdaterService;

    public ServiceTypePutController(ServiceTypeUpdaterService serviceTypeUpdaterService) {
        this.serviceTypeUpdaterService = serviceTypeUpdaterService;
    }

    /**
     * Actualiza un tipo de servicio existente.
     *
     * @param request datos validados con el nuevo estado del tipo de servicio.
     * @param id identificador del tipo de servicio a actualizar.
     * @return 200 OK con el tipo de servicio actualizado.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ServiceTypeResponse> update(@RequestBody @Valid ServiceTypeRequest request, @PathVariable Long id){
        ServiceType serviceType = serviceTypeUpdaterService.update(request, id);
        ServiceTypeResponse response = ServiceTypeResponse.toResponse(serviceType);
        return ResponseEntity.ok(response);
    }
}
