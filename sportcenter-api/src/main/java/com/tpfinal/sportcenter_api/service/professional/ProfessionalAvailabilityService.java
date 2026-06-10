package com.tpfinal.sportcenter_api.service.professional;

import com.tpfinal.sportcenter_api.dto.response.professional.ProfessionalAvailabilityResponse;
import com.tpfinal.sportcenter_api.dto.response.professional.ProfessionalAvailabilityResponse.BusySlot;
import com.tpfinal.sportcenter_api.entity.appointment.Appointment;
import com.tpfinal.sportcenter_api.enums.appointment.AppointmentStatusEnum;
import com.tpfinal.sportcenter_api.repository.appointment.JpaAppointmentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio de consulta de la agenda ocupada de un profesional en un día.
 *
 * Devuelve solo los rangos horarios de los turnos activos (PENDING/CONFIRMED):
 * los cancelados liberan el horario, igual que en el chequeo de solapamiento.
 * El frontend usa esto para mostrar qué horarios están tomados antes de
 * reservar, en lugar de intentar a ciegas y recibir un 409.
 */
@Service
public class ProfessionalAvailabilityService {

    private final JpaAppointmentRepository jpaAppointmentRepository;
    private final ProfessionalFinderService professionalFinderService;

    public ProfessionalAvailabilityService(JpaAppointmentRepository jpaAppointmentRepository,
                                           ProfessionalFinderService professionalFinderService) {
        this.jpaAppointmentRepository = jpaAppointmentRepository;
        this.professionalFinderService = professionalFinderService;
    }

    /**
     * Lista los rangos ocupados del profesional para el día dado, ordenados
     * por hora de inicio. Si el profesional no existe responde 404 (vía
     * ProfessionalFinderService): un id inexistente no debe parecer un día libre.
     */
    public ProfessionalAvailabilityResponse findBusySlots(Long professionalId, LocalDate date) {
        professionalFinderService.find(professionalId);

        // Ventana [00:00 del día, 00:00 del día siguiente): la misma condición de
        // solapamiento de los exists también captura turnos que cruzan medianoche.
        LocalDateTime dayStart = date.atStartOfDay();
        LocalDateTime dayEnd = date.plusDays(1).atStartOfDay();

        List<Appointment> appointments = jpaAppointmentRepository
                .findByProfessionalIdAndStartTimeBeforeAndEndTimeAfterAndStatusNotOrderByStartTimeAsc(
                        professionalId, dayEnd, dayStart, AppointmentStatusEnum.CANCELLED);

        List<BusySlot> busySlots = appointments.stream()
                .map(a -> new BusySlot(a.getStartTime(), a.getEndTime()))
                .toList();

        return new ProfessionalAvailabilityResponse(professionalId, date, busySlots);
    }
}
