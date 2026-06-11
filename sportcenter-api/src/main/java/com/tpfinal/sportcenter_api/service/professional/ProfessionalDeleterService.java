package com.tpfinal.sportcenter_api.service.professional;

import com.tpfinal.sportcenter_api.entity.professional.Professional;
import com.tpfinal.sportcenter_api.exception.professional.ProfessionalHasAppointmentsException;
import com.tpfinal.sportcenter_api.repository.appointment.JpaAppointmentRepository;
import com.tpfinal.sportcenter_api.repository.professional.JpaProfessionalRepository;
import org.springframework.stereotype.Service;

/**
 * Servicio encargado de eliminar profesionales por ID.
 *
 * Un profesional con turnos asociados (en cualquier estado) no se borra:
 * además de violar la FK, se perdería el historial de esos turnos. El
 * precheck devuelve un 409 con mensaje claro; la alternativa correcta es
 * desactivarlo (active = false), que bloquea reservas nuevas sin tocar nada.
 */
@Service
public class ProfessionalDeleterService {
    private final JpaProfessionalRepository jpaProfessionalRepository;
    private final JpaAppointmentRepository jpaAppointmentRepository;
    private final ProfessionalFinderService professionalFinderService;

    public ProfessionalDeleterService(JpaProfessionalRepository jpaProfessionalRepository,
                                      JpaAppointmentRepository jpaAppointmentRepository,
                                      ProfessionalFinderService professionalFinderService) {
        this.jpaProfessionalRepository = jpaProfessionalRepository;
        this.jpaAppointmentRepository = jpaAppointmentRepository;
        this.professionalFinderService = professionalFinderService;
    }

    /**
     * Elimina el profesional con el ID indicado, si no tiene turnos asociados.
     */
    public void delete(Long id){
        Professional professional = professionalFinderService.find(id);

        if (jpaAppointmentRepository.existsByProfessionalId(id)) {
            throw new ProfessionalHasAppointmentsException(id);
        }

        jpaProfessionalRepository.delete(professional);
    }
}
