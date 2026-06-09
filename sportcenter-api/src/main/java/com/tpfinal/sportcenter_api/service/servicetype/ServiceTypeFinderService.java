package com.tpfinal.sportcenter_api.service.servicetype;

import com.tpfinal.sportcenter_api.entity.servicetype.ServiceType;
import com.tpfinal.sportcenter_api.exception.servicetype.ServiceTypeNotFoundException;
import com.tpfinal.sportcenter_api.repository.servicetype.JpaServiceTypeRepository;
import org.springframework.stereotype.Service;

/**
 * Servicio de consulta que recupera un tipo de servicio por su ID.
 * Centraliza el manejo de "no encontrado" para otros servicios.
 */
@Service
public class ServiceTypeFinderService {
    private final JpaServiceTypeRepository jpaServiceTypeRepository;

    public ServiceTypeFinderService(JpaServiceTypeRepository jpaServiceTypeRepository) {
        this.jpaServiceTypeRepository = jpaServiceTypeRepository;
    }

    /**
     * Busca un tipo de servicio por su ID.
     */
    public ServiceType find(Long id){
        ServiceType serviceType = jpaServiceTypeRepository.findById(id).orElseThrow(() -> new ServiceTypeNotFoundException(id));
        return serviceType;
    }
}
