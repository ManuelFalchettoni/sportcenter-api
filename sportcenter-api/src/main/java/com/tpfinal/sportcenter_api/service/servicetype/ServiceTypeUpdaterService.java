package com.tpfinal.sportcenter_api.service.servicetype;

import com.tpfinal.sportcenter_api.dto.request.servicetype.ServiceTypeRequest;
import com.tpfinal.sportcenter_api.entity.servicetype.ServiceType;
import com.tpfinal.sportcenter_api.repository.servicetype.JpaServiceTypeRepository;
import org.springframework.stereotype.Service;

/**
 * Servicio encargado de actualizar tipos de servicio existentes.
 * Reemplaza nombre, duración y precio.
 */
@Service
public class ServiceTypeUpdaterService {
    private final JpaServiceTypeRepository jpaServiceTypeRepository;
    private final ServiceTypeFinderService serviceTypeFinderService;

    public ServiceTypeUpdaterService(JpaServiceTypeRepository jpaServiceTypeRepository, ServiceTypeFinderService serviceTypeFinderService) {
        this.jpaServiceTypeRepository = jpaServiceTypeRepository;
        this.serviceTypeFinderService = serviceTypeFinderService;
    }

    /**
     * Actualiza los datos del tipo de servicio identificado por el ID.
     */
    public ServiceType update(ServiceTypeRequest request, Long id){
        ServiceType serviceType = serviceTypeFinderService.find(id);
        serviceType.setName(request.getName());
        serviceType.setDurationMinutes(request.getDurationMinutes());
        serviceType.setPrice(request.getPrice());
        return jpaServiceTypeRepository.save(serviceType);
    }
}
