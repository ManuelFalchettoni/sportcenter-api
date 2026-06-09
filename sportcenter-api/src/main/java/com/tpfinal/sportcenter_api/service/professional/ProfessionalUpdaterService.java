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
 * Servicio encargado de actualizar profesionales existentes.
 * <p>
 * Reemplaza nombre, especialidad, estado activo y los tipos de servicio
 * asociados (resueltos a partir de sus IDs).
 */
@Service
public class ProfessionalUpdaterService {
    private final JpaProfessionalRepository jpaProfessionalRepository;
    private final ProfessionalFinderService professionalFinderService;
    private final JpaServiceTypeRepository jpaServiceTypeRepository;

    public ProfessionalUpdaterService(JpaProfessionalRepository jpaProfessionalRepository,
                                      ProfessionalFinderService professionalFinderService,
                                      JpaServiceTypeRepository jpaServiceTypeRepository) {
        this.jpaProfessionalRepository = jpaProfessionalRepository;
        this.professionalFinderService = professionalFinderService;
        this.jpaServiceTypeRepository = jpaServiceTypeRepository;
    }

    /**
     * Actualiza los datos del profesional identificado por el ID.
     */
    public Professional update(ProfessionalRequest request, Long id) {
        Professional professional = professionalFinderService.find(id);
        professional.setName(request.getName());
        professional.setSpeciality(request.getSpeciality());
        professional.setActive(request.getActive());
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
