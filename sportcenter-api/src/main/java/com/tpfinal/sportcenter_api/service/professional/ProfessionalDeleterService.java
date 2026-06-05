package com.tpfinal.sportcenter_api.service.professional;

import com.tpfinal.sportcenter_api.entity.professional.Professional;
import com.tpfinal.sportcenter_api.repository.professional.JpaProfessionalRepository;
import org.springframework.stereotype.Service;

/**
 * Servicio encargado de eliminar profesionales por ID.
 */
@Service
public class ProfessionalDeleterService {
    private final JpaProfessionalRepository jpaProfessionalRepository;
    private final ProfessionalFinderService professionalFinderService;

    public ProfessionalDeleterService(JpaProfessionalRepository jpaProfessionalRepository, ProfessionalFinderService professionalFinderService) {
        this.jpaProfessionalRepository = jpaProfessionalRepository;
        this.professionalFinderService = professionalFinderService;
    }

    /**
     * Elimina el profesional con el ID indicado.
     *
     * @param id identificador del profesional.
     * @throws com.tpfinal.sportcenter_api.exception.professional.ProfessionalNotFoundException
     *         si el profesional no existe.
     */
    public void delete(Long id){
        Professional professional = professionalFinderService.find(id);
        jpaProfessionalRepository.delete(professional);
    }
}
