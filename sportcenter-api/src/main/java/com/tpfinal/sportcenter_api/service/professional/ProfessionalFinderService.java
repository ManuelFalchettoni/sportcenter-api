package com.tpfinal.sportcenter_api.service.professional;

import com.tpfinal.sportcenter_api.entity.professional.Professional;
import com.tpfinal.sportcenter_api.exception.professional.ProfessionalNotFoundException;
import com.tpfinal.sportcenter_api.repository.professional.JpaProfessionalRepository;
import org.springframework.stereotype.Service;

/**
 * Servicio de consulta que recupera un profesional individual por su ID.
 * Centraliza el manejo de "no encontrado" para otros servicios.
 */
@Service
public class ProfessionalFinderService {
    private final JpaProfessionalRepository jpaProfessionalRepository;

    public ProfessionalFinderService(JpaProfessionalRepository jpaProfessionalRepository) {
        this.jpaProfessionalRepository = jpaProfessionalRepository;
    }

    /**
     * Busca un profesional por su ID.
     *
     * @param id identificador del profesional.
     * @return el profesional correspondiente.
     * @throws com.tpfinal.sportcenter_api.exception.professional.ProfessionalNotFoundException
     *         si no existe.
     */
    public Professional find(Long id){
        Professional professional =  jpaProfessionalRepository.findById(id).orElseThrow(() -> new ProfessionalNotFoundException(id));
        return professional;
    }
}
