package com.tpfinal.sportcenter_api.service.servicetype;

import com.tpfinal.sportcenter_api.entity.servicetype.ServiceType;
import com.tpfinal.sportcenter_api.exception.servicetype.ServiceTypeInUseException;
import com.tpfinal.sportcenter_api.repository.appointment.JpaAppointmentRepository;
import com.tpfinal.sportcenter_api.repository.professional.JpaProfessionalRepository;
import com.tpfinal.sportcenter_api.repository.servicetype.JpaServiceTypeRepository;
import org.springframework.stereotype.Service;

/**
 * Servicio encargado de eliminar tipos de servicio por ID.
 *
 * Un service type en uso no se borra; el precheck devuelve un 409 con un
 * mensaje que dice qué lo bloquea. Dos cosas pueden referenciarlo:
 * - Turnos (FK appointment.service_type_id), en cualquier estado.
 * - Profesionales que lo ofrecen (tabla de unión professional_service_types:
 *   Hibernate no limpia el lado no-dueño del @ManyToMany al borrar).
 */
@Service
public class ServiceTypeDeleterService {
    private final JpaServiceTypeRepository jpaServiceTypeRepository;
    private final JpaAppointmentRepository jpaAppointmentRepository;
    private final JpaProfessionalRepository jpaProfessionalRepository;
    private final ServiceTypeFinderService serviceTypeFinderService;

    public ServiceTypeDeleterService(JpaServiceTypeRepository jpaServiceTypeRepository,
                                     JpaAppointmentRepository jpaAppointmentRepository,
                                     JpaProfessionalRepository jpaProfessionalRepository,
                                     ServiceTypeFinderService serviceTypeFinderService) {
        this.jpaServiceTypeRepository = jpaServiceTypeRepository;
        this.jpaAppointmentRepository = jpaAppointmentRepository;
        this.jpaProfessionalRepository = jpaProfessionalRepository;
        this.serviceTypeFinderService = serviceTypeFinderService;
    }

    /**
     * Elimina el tipo de servicio con el ID indicado, si nada lo referencia.
     */
    public void delete(Long id){
        ServiceType serviceType = serviceTypeFinderService.find(id);

        if (jpaAppointmentRepository.existsByServiceTypeId(id)) {
            throw ServiceTypeInUseException.referencedByAppointments(id);
        }
        if (jpaProfessionalRepository.existsByServicesId(id)) {
            throw ServiceTypeInUseException.offeredByProfessionals(id);
        }

        jpaServiceTypeRepository.delete(serviceType);
    }
}
