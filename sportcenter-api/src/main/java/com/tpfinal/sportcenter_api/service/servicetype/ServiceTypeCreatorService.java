package com.tpfinal.sportcenter_api.service.servicetype;

import com.tpfinal.sportcenter_api.dto.request.servicetype.ServiceTypeRequest;
import com.tpfinal.sportcenter_api.entity.servicetype.ServiceType;
import com.tpfinal.sportcenter_api.repository.servicetype.JpaServiceTypeRepository;
import org.springframework.stereotype.Service;

/**
 * Servicio encargado de crear nuevos tipos de servicio ofrecidos por el centro.
 */
@Service
public class ServiceTypeCreatorService {
    private final JpaServiceTypeRepository jpaServiceTypeRepository;

    public ServiceTypeCreatorService(JpaServiceTypeRepository jpaServiceTypeRepository) {
        this.jpaServiceTypeRepository = jpaServiceTypeRepository;
    }

    /**
     * Crea y persiste un nuevo tipo de servicio.
     *
     * @param request datos del tipo de servicio a crear.
     * @return el tipo de servicio persistido con su ID generado.
     */
    public ServiceType create(ServiceTypeRequest request){
        return jpaServiceTypeRepository.save(ServiceTypeRequest.fromRequest(request));
    }
}
