package com.tpfinal.sportcenter_api.exception.appointment;

// El status (404) y el body los asigna GlobalExceptionHandler, que es la única
// fuente de verdad del manejo de errores. Por eso no lleva @ResponseStatus.
public class AppointmentNotFoundException extends RuntimeException{

    public AppointmentNotFoundException(Long id){
        super("Appointment not found. ID:" + id);
    }
}
