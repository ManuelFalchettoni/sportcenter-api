package com.tpfinal.sportcenter_api.service.professional;

import com.tpfinal.sportcenter_api.entity.professional.Professional;
import com.tpfinal.sportcenter_api.exception.professional.ProfessionalNotFoundException;
import com.tpfinal.sportcenter_api.repository.professional.JpaProfessionalRepository;
import org.springframework.stereotype.Service;

@Service
public class ProfessionalFinderService {
    private final JpaProfessionalRepository jpaProfessionalRepository;

    public ProfessionalFinderService(JpaProfessionalRepository jpaProfessionalRepository) {
        this.jpaProfessionalRepository = jpaProfessionalRepository;
    }

    public Professional find(Long id){
        Professional professional =  jpaProfessionalRepository.findById(id).orElseThrow(() -> new ProfessionalNotFoundException(id));
        return professional;
    }
}
