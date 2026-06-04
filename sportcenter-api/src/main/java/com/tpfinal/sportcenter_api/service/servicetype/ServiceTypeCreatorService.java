package com.tpfinal.sportcenter_api.service.servicetype;

import com.tpfinal.sportcenter_api.dto.request.servicetype.ServiceTypeRequest;
import com.tpfinal.sportcenter_api.entity.servicetype.ServiceType;
import com.tpfinal.sportcenter_api.repository.servicetype.JpaServiceTypeRepository;
import org.springframework.stereotype.Service;

@Service
public class ServiceTypeCreatorService {
    private final JpaServiceTypeRepository jpaServiceTypeRepository;

    public ServiceTypeCreatorService(JpaServiceTypeRepository jpaServiceTypeRepository) {
        this.jpaServiceTypeRepository = jpaServiceTypeRepository;
    }

    public ServiceType create(ServiceTypeRequest request){
        return jpaServiceTypeRepository.save(ServiceTypeRequest.fromRequest(request));
    }
}
