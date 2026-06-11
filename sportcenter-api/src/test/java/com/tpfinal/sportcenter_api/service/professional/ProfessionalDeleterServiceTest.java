package com.tpfinal.sportcenter_api.service.professional;

import com.tpfinal.sportcenter_api.entity.professional.Professional;
import com.tpfinal.sportcenter_api.exception.professional.ProfessionalHasAppointmentsException;
import com.tpfinal.sportcenter_api.exception.professional.ProfessionalNotFoundException;
import com.tpfinal.sportcenter_api.repository.appointment.JpaAppointmentRepository;
import com.tpfinal.sportcenter_api.repository.professional.JpaProfessionalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Test unitario con Mockito puro: sin Spring ni base de datos.
@ExtendWith(MockitoExtension.class)
class ProfessionalDeleterServiceTest {

    @Mock
    private JpaProfessionalRepository jpaProfessionalRepository;
    @Mock
    private JpaAppointmentRepository jpaAppointmentRepository;
    @Mock
    private ProfessionalFinderService professionalFinderService;

    @InjectMocks
    private ProfessionalDeleterService service;

    private Professional professional;

    @BeforeEach
    void setUp() {
        professional = new Professional("Dr. House", "Clinica", true);
        professional.setId(2L);
    }

    // Sin turnos asociados -> se borra.
    @Test
    void delete_removesProfessionalWithoutAppointments() {
        when(professionalFinderService.find(2L)).thenReturn(professional);
        when(jpaAppointmentRepository.existsByProfessionalId(2L)).thenReturn(false);

        service.delete(2L);

        verify(jpaProfessionalRepository).delete(professional);
    }

    // Con turnos (de cualquier estado) -> 409 con mensaje accionable, sin borrar.
    @Test
    void delete_throwsWhenProfessionalHasAppointments() {
        when(professionalFinderService.find(2L)).thenReturn(professional);
        when(jpaAppointmentRepository.existsByProfessionalId(2L)).thenReturn(true);

        assertThatThrownBy(() -> service.delete(2L))
                .isInstanceOf(ProfessionalHasAppointmentsException.class)
                .hasMessageContaining("Deactivate it");

        verify(jpaProfessionalRepository, never()).delete(any(Professional.class));
    }

    // Id inexistente -> el 404 del finder se propaga sin tocar nada.
    @Test
    void delete_propagatesNotFound() {
        when(professionalFinderService.find(99L)).thenThrow(new ProfessionalNotFoundException(99L));

        assertThatThrownBy(() -> service.delete(99L))
                .isInstanceOf(ProfessionalNotFoundException.class);

        verify(jpaProfessionalRepository, never()).delete(any(Professional.class));
    }
}
