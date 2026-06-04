package com.tpfinal.sportcenter_api.service.professional;

import com.tpfinal.sportcenter_api.entity.professional.Professional;
import com.tpfinal.sportcenter_api.repository.professional.JpaProfessionalRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProfessionalGetAllService {
    private final JpaProfessionalRepository jpaProfessionalRepository;

    public ProfessionalGetAllService(JpaProfessionalRepository jpaProfessionalRepository) {
        this.jpaProfessionalRepository = jpaProfessionalRepository;
    }

    public Page<Professional> findAll(Pageable pageable) {
        return jpaProfessionalRepository.findAll(pageable);
    }
}
