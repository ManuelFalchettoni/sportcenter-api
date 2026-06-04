package com.tpfinal.sportcenter_api.service.professional;

import com.tpfinal.sportcenter_api.dto.request.professional.ProfessionalRequest;
import com.tpfinal.sportcenter_api.entity.professional.Professional;
import com.tpfinal.sportcenter_api.repository.professional.JpaProfessionalRepository;
import org.springframework.stereotype.Service;

@Service
public class ProfessionalCreatorService {
    private final JpaProfessionalRepository jpaProfessionalRepository;

    public ProfessionalCreatorService(JpaProfessionalRepository jpaProfessionalRepository) {
        this.jpaProfessionalRepository = jpaProfessionalRepository;
    }

    public Professional create(ProfessionalRequest request){
        return jpaProfessionalRepository.save(ProfessionalRequest.fromRequest(request));
    }
}
