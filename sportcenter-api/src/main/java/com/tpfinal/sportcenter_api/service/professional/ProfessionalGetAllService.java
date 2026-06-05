package com.tpfinal.sportcenter_api.service.professional;

import com.tpfinal.sportcenter_api.entity.professional.Professional;
import com.tpfinal.sportcenter_api.repository.professional.JpaProfessionalRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Servicio que expone el listado paginado de profesionales.
 */
@Service
public class ProfessionalGetAllService {
    private final JpaProfessionalRepository jpaProfessionalRepository;

    public ProfessionalGetAllService(JpaProfessionalRepository jpaProfessionalRepository) {
        this.jpaProfessionalRepository = jpaProfessionalRepository;
    }

    /**
     * Devuelve la página de profesionales solicitada.
     *
     * @param pageable parámetros de paginación y ordenamiento.
     * @return página con los profesionales correspondientes.
     */
    public Page<Professional> findAll(Pageable pageable) {
        return jpaProfessionalRepository.findAll(pageable);
    }
}
