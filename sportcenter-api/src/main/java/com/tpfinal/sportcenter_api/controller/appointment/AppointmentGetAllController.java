package com.tpfinal.sportcenter_api.controller.appointment;

import com.tpfinal.sportcenter_api.config.UserPrincipal;
import com.tpfinal.sportcenter_api.dto.request.appointment.AppointmentFilterRequest;
import com.tpfinal.sportcenter_api.dto.response.appointment.AppointmentResponse;
import com.tpfinal.sportcenter_api.enums.appointment.AppointmentStatusEnum;
import com.tpfinal.sportcenter_api.service.appointment.AppointmentGetAllService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

/**
 * Controlador REST que expone el listado paginado de turnos, con filtros
 * opcionales por rango de fechas, estado y profesional.
 * Ruta base: /sportcenter/appointments.
 */
@RestController
@RequestMapping("/sportcenter/appointments")
public class AppointmentGetAllController {
    private final AppointmentGetAllService appointmentGetAllService;

    public AppointmentGetAllController(AppointmentGetAllService appointmentGetAllService) {
        this.appointmentGetAllService = appointmentGetAllService;
    }

    /**
     * Lista turnos en forma paginada. Un ADMIN ve todos; un USER los propios.
     * Filtros opcionales (se combinan con AND):
     * - from / to: rango sobre startTime, inclusive, en ISO yyyy-MM-dd'T'HH:mm:ss.
     * - status: PENDING | CONFIRMED | CANCELLED.
     * - professionalId: turnos de un profesional.
     * - query: búsqueda libre (case-insensitive) sobre notas, nombre del
     *   profesional, nombre del tipo de servicio o username del dueño.
     * Un valor mal formado responde 400 (handler de type mismatch).
     */
    @GetMapping
    public ResponseEntity<Page<AppointmentResponse>> findAll(
            Pageable pageable,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(required = false) AppointmentStatusEnum status,
            @RequestParam(required = false) Long professionalId,
            @RequestParam(required = false) String query,
            @AuthenticationPrincipal UserPrincipal principal) {
        AppointmentFilterRequest filter = new AppointmentFilterRequest(from, to, status, professionalId, query);
        Page<AppointmentResponse> response = appointmentGetAllService
                .findAll(pageable, principal.getUser(), filter)
                .map(AppointmentResponse::toResponse);
        return ResponseEntity.ok(response);
    }

    /**
     * Lista los turnos del usuario autenticado, sin importar su rol: un ADMIN
     * también ve solo los propios (su agenda personal), no los de todo el centro.
     * Acepta los mismos filtros opcionales que findAll (se combinan con AND);
     * la ruta literal /me gana sobre /{id}, así que no hay ambigüedad de ruteo.
     */
    @GetMapping("/me")
    public ResponseEntity<Page<AppointmentResponse>> findMine(
            Pageable pageable,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(required = false) AppointmentStatusEnum status,
            @RequestParam(required = false) Long professionalId,
            @RequestParam(required = false) String query,
            @AuthenticationPrincipal UserPrincipal principal) {
        AppointmentFilterRequest filter = new AppointmentFilterRequest(from, to, status, professionalId, query);
        Page<AppointmentResponse> response = appointmentGetAllService
                .findMine(pageable, principal.getUser(), filter)
                .map(AppointmentResponse::toResponse);
        return ResponseEntity.ok(response);
    }
}
