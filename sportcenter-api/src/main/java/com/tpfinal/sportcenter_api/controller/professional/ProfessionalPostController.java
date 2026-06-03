package com.tpfinal.sportcenter_api.controller.professional;

import com.tpfinal.sportcenter_api.dto.request.professional.ProfessionalRequest;
import com.tpfinal.sportcenter_api.dto.response.professional.ProfessionalResponse;
import com.tpfinal.sportcenter_api.service.professional.ProfessionalCreatorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sportcenter/professional")
public class ProfessionalPostController {
    private final ProfessionalCreatorService professionalCreatorService;

    public ProfessionalPostController(ProfessionalCreatorService professionalCreatorService) {
        this.professionalCreatorService = professionalCreatorService;
    }

    @PostMapping
    public ResponseEntity<ProfessionalResponse> create(@RequestBody ProfessionalRequest request){
        ProfessionalResponse response = ProfessionalResponse.toResponse(professionalCreatorService.create(request));
        return ResponseEntity.ok(response);
    }
}
