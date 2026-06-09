package com.tpfinal.sportcenter_api.service.servicetype;

import com.tpfinal.sportcenter_api.entity.servicetype.ServiceType;
import com.tpfinal.sportcenter_api.repository.servicetype.JpaServiceTypeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Servicio que expone el listado paginado de tipos de servicio.
 */
@Service
public class ServiceTypeGetAllService {
    private final JpaServiceTypeRepository jpaServiceTypeRepository;

    public ServiceTypeGetAllService(JpaServiceTypeRepository jpaServiceTypeRepository) {
        this.jpaServiceTypeRepository = jpaServiceTypeRepository;
    }

    /**
     * Devuelve la página de tipos de servicio solicitada.
     */
    public Page<ServiceType> findAll(Pageable pageable) {
        return jpaServiceTypeRepository.findAll(pageable);
    }
}
