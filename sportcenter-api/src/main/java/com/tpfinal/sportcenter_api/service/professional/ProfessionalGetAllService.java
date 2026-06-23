package com.tpfinal.sportcenter_api.service.professional;

import com.tpfinal.sportcenter_api.entity.professional.Professional;
import com.tpfinal.sportcenter_api.repository.professional.JpaProfessionalRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Servicio que expone el listado paginado de profesionales, con búsqueda
 * opcional por texto sobre nombre y especialidad (query general) o por campo
 * individual.
 */
@Service
public class ProfessionalGetAllService {
    private final JpaProfessionalRepository jpaProfessionalRepository;

    public ProfessionalGetAllService(JpaProfessionalRepository jpaProfessionalRepository) {
        this.jpaProfessionalRepository = jpaProfessionalRepository;
    }

    /**
     * Devuelve la página de profesionales solicitada. Los filtros opcionales se
     * combinan con AND (los null/vacíos no filtran):
     * - query: coincidencia (case-insensitive) sobre nombre o especialidad.
     * - name: coincidencia (case-insensitive) sobre nombre.
     * - speciality: coincidencia (case-insensitive) sobre especialidad.
     */
    public Page<Professional> findAll(Pageable pageable, String query, String name, String speciality) {
        List<Specification<Professional>> specs = new ArrayList<>();
        if (query != null && !query.isBlank()) {
            specs.add(ProfessionalSpecifications.matchesQuery(query.trim()));
        }
        if (name != null && !name.isBlank()) {
            specs.add(ProfessionalSpecifications.nameContains(name.trim()));
        }
        if (speciality != null && !speciality.isBlank()) {
            specs.add(ProfessionalSpecifications.specialityContains(speciality.trim()));
        }
        // allOf combina con AND; con la lista vacía no restringe nada.
        return jpaProfessionalRepository.findAll(Specification.allOf(specs), pageable);
    }
}
