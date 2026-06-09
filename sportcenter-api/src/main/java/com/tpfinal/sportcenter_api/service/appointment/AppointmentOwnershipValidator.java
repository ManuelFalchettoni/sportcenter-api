package com.tpfinal.sportcenter_api.service.appointment;

import com.tpfinal.sportcenter_api.entity.appointment.Appointment;
import com.tpfinal.sportcenter_api.entity.user.User;
import com.tpfinal.sportcenter_api.enums.user.UserEnum;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

/**
 * Regla de ownership de turnos: solo el dueño o un ADMIN pueden operar
 * sobre un turno existente. La comparten los services de consulta,
 * actualización, cancelación y borrado.
 *
 * <p>AccessDeniedException ya está mapeada a 403 Forbidden en
 * GlobalExceptionHandler.
 */
@Component
public class AppointmentOwnershipValidator {

    /**
     * Tira AccessDeniedException si el caller no es ADMIN ni el dueño.
     */
    public void check(Appointment appointment, User caller) {
        boolean isAdmin = caller.getRole() == UserEnum.ADMIN;
        boolean isOwner = appointment.getUser().getId().equals(caller.getId());
        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("Appointment does not belong to the authenticated user.");
        }
    }
}
