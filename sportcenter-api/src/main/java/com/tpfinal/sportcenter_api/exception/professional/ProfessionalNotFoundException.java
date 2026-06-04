package com.tpfinal.sportcenter_api.exception.professional;

public class ProfessionalNotFoundException extends RuntimeException {
    public ProfessionalNotFoundException(Long id) {
        super("Professional with id: "+id+" not found.");
    }
}
