package com.tpfinal.sportcenter_api.service.servicetype;

import com.tpfinal.sportcenter_api.entity.servicetype.ServiceType;
import com.tpfinal.sportcenter_api.repository.servicetype.JpaServiceTypeRepository;
import org.springframework.stereotype.Service;

@Service
public class ServiceTypeDeleterService {
    private final JpaServiceTypeRepository jpaServiceTypeRepository;
    private final ServiceTypeFinderService serviceTypeFinderService;

    public ServiceTypeDeleterService(JpaServiceTypeRepository jpaServiceTypeRepository, ServiceTypeFinderService serviceTypeFinderService) {
        this.jpaServiceTypeRepository = jpaServiceTypeRepository;
        this.serviceTypeFinderService = serviceTypeFinderService;
    }

    public void delete(Long id){
        ServiceType serviceType = serviceTypeFinderService.find(id);
        jpaServiceTypeRepository.delete(serviceType);
    }
}
