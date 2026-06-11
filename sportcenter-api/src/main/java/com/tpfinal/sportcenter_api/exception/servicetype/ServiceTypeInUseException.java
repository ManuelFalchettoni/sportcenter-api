package com.tpfinal.sportcenter_api.exception.servicetype;

/**
 * Se lanza al intentar borrar un service type que sigue en uso, ya sea por
 * turnos que lo referencian o por profesionales que lo ofrecen (filas en la
 * tabla de unión professional_service_types).
 */
public class ServiceTypeInUseException extends RuntimeException {

    private ServiceTypeInUseException(String message) {
        super(message);
    }

    public static ServiceTypeInUseException referencedByAppointments(Long id) {
        return new ServiceTypeInUseException(
                "The service type with id: " + id + " cannot be deleted because it has appointments.");
    }

    public static ServiceTypeInUseException offeredByProfessionals(Long id) {
        return new ServiceTypeInUseException(
                "The service type with id: " + id + " cannot be deleted because professionals offer it. "
                        + "Remove it from those professionals first.");
    }
}
