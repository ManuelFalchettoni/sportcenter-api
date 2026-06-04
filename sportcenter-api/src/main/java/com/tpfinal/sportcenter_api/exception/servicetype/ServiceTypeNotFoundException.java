package com.tpfinal.sportcenter_api.exception.servicetype;

public class ServiceTypeNotFoundException extends RuntimeException {
    public ServiceTypeNotFoundException(Long id) {
        super("ServiceType with id: "+id+" not found.");
    }
}
