package com.tpfinal.sportcenter_api.service.servicetype;

import com.tpfinal.sportcenter_api.entity.servicetype.ServiceType;
import com.tpfinal.sportcenter_api.exception.servicetype.ServiceTypeNotFoundException;
import com.tpfinal.sportcenter_api.repository.servicetype.JpaServiceTypeRepository;
import org.springframework.stereotype.Service;

@Service
public class ServiceTypeFinderService {
    private final JpaServiceTypeRepository jpaServiceTypeRepository;

    public ServiceTypeFinderService(JpaServiceTypeRepository jpaServiceTypeRepository) {
        this.jpaServiceTypeRepository = jpaServiceTypeRepository;
    }

    public ServiceType find(Long id){
        ServiceType serviceType = jpaServiceTypeRepository.findById(id).orElseThrow(() -> new ServiceTypeNotFoundException(id));
        return serviceType;
    }
}
