package com.tpfinal.sportcenter_api.service.servicetype;

import com.tpfinal.sportcenter_api.entity.servicetype.ServiceType;
import com.tpfinal.sportcenter_api.exception.servicetype.ServiceTypeInUseException;
import com.tpfinal.sportcenter_api.repository.appointment.JpaAppointmentRepository;
import com.tpfinal.sportcenter_api.repository.professional.JpaProfessionalRepository;
import com.tpfinal.sportcenter_api.repository.servicetype.JpaServiceTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Test unitario con Mockito puro: sin Spring ni base de datos.
@ExtendWith(MockitoExtension.class)
class ServiceTypeDeleterServiceTest {

    @Mock
    private JpaServiceTypeRepository jpaServiceTypeRepository;
    @Mock
    private JpaAppointmentRepository jpaAppointmentRepository;
    @Mock
    private JpaProfessionalRepository jpaProfessionalRepository;
    @Mock
    private ServiceTypeFinderService serviceTypeFinderService;

    @InjectMocks
    private ServiceTypeDeleterService service;

    private ServiceType serviceType;

    @BeforeEach
    void setUp() {
        serviceType = new ServiceType("Masaje", 60, new BigDecimal("100.00"));
        serviceType.setId(3L);
    }

    // Sin turnos ni profesionales que lo ofrezcan -> se borra.
    @Test
    void delete_removesServiceTypeWhenUnused() {
        when(serviceTypeFinderService.find(3L)).thenReturn(serviceType);
        when(jpaAppointmentRepository.existsByServiceTypeId(3L)).thenReturn(false);
        when(jpaProfessionalRepository.existsByServicesId(3L)).thenReturn(false);

        service.delete(3L);

        verify(jpaServiceTypeRepository).delete(serviceType);
    }

    // Referenciado por turnos -> 409, sin borrar.
    @Test
    void delete_throwsWhenReferencedByAppointments() {
        when(serviceTypeFinderService.find(3L)).thenReturn(serviceType);
        when(jpaAppointmentRepository.existsByServiceTypeId(3L)).thenReturn(true);

        assertThatThrownBy(() -> service.delete(3L))
                .isInstanceOf(ServiceTypeInUseException.class)
                .hasMessageContaining("appointments");

        verify(jpaServiceTypeRepository, never()).delete(any(ServiceType.class));
    }

    // Ofrecido por profesionales (tabla de unión) -> 409 con mensaje accionable.
    @Test
    void delete_throwsWhenOfferedByProfessionals() {
        when(serviceTypeFinderService.find(3L)).thenReturn(serviceType);
        when(jpaAppointmentRepository.existsByServiceTypeId(3L)).thenReturn(false);
        when(jpaProfessionalRepository.existsByServicesId(3L)).thenReturn(true);

        assertThatThrownBy(() -> service.delete(3L))
                .isInstanceOf(ServiceTypeInUseException.class)
                .hasMessageContaining("professionals");

        verify(jpaServiceTypeRepository, never()).delete(any(ServiceType.class));
    }
}
