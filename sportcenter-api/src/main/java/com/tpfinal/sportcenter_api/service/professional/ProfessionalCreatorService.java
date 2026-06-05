package com.tpfinal.sportcenter_api.service.professional;

import com.tpfinal.sportcenter_api.dto.request.professional.ProfessionalRequest;
import com.tpfinal.sportcenter_api.entity.professional.Professional;
import com.tpfinal.sportcenter_api.entity.servicetype.ServiceType;
import com.tpfinal.sportcenter_api.exception.servicetype.ServiceTypeNotFoundException;
import com.tpfinal.sportcenter_api.repository.professional.JpaProfessionalRepository;
import com.tpfinal.sportcenter_api.repository.servicetype.JpaServiceTypeRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * Servicio encargado de dar de alta nuevos profesionales.
 * <p>
 * Resuelve los tipos de servicio asociados a partir de sus IDs antes de persistir.
 */
@Service
public class ProfessionalCreatorService {
    private final JpaProfessionalRepository jpaProfessionalRepository;
    private final JpaServiceTypeRepository jpaServiceTypeRepository;

    public ProfessionalCreatorService(JpaProfessionalRepository jpaProfessionalRepository,
                                      JpaServiceTypeRepository jpaServiceTypeRepository) {
        this.jpaProfessionalRepository = jpaProfessionalRepository;
        this.jpaServiceTypeRepository = jpaServiceTypeRepository;
    }

    /**
     * Crea y persiste un nuevo profesional con los tipos de servicio indicados.
     *
     * @param request datos del profesional a crear.
     * @return el profesional persistido con su ID generado.
     * @throws com.tpfinal.sportcenter_api.exception.servicetype.ServiceTypeNotFoundException
     *         si alguno de los IDs de servicio no existe.
     */
    public Professional create(ProfessionalRequest request) {
        Professional professional = ProfessionalRequest.fromRequest(request);
        professional.setServices(resolveServices(request.getServiceTypeIds()));
        return jpaProfessionalRepository.save(professional);
    }

    private Set<ServiceType> resolveServices(Set<Long> ids) {
        Set<ServiceType> result = new HashSet<>();
        if (ids == null) return result;
        for (Long id : ids) {
            ServiceType service = jpaServiceTypeRepository.findById(id)
                    .orElseThrow(() -> new ServiceTypeNotFoundException(id));
            result.add(service);
        }
        return result;
    }
}
