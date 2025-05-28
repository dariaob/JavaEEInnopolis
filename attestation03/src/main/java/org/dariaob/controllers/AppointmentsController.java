package org.dariaob.controllers;

import lombok.RequiredArgsConstructor;
import org.dariaob.dto.appointments.AppointmentResponseDto;
import org.dariaob.services.AppointmentsService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
public class AppointmentsController {

    private final AppointmentsService appointmentsService;

    @GetMapping("/active")
    public List<AppointmentResponseDto> getAllActiveAppointments() {
        return appointmentsService.getAllActiveAppointments().stream()
                .map(AppointmentResponseDto::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public AppointmentResponseDto getActiveAppointmentById(@PathVariable Long id) {
        return new AppointmentResponseDto(appointmentsService.getActiveAppointmentById(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void softDeleteAppointment(@PathVariable Long id) {
        appointmentsService.softDeleteAppointment(id);
    }

    @PutMapping("/{id}/restore")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void restoreAppointment(@PathVariable Long id) {
        appointmentsService.restoreAppointment(id);
    }

    @GetMapping("/doctor/{doctorId}")
    public List<AppointmentResponseDto> getActiveAppointmentsByDoctor(@PathVariable Long doctorId) {
        return appointmentsService.getActiveAppointmentsByDoctor(doctorId).stream()
                .map(AppointmentResponseDto::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/patient/{patientId}")
    public List<AppointmentResponseDto> getActiveAppointmentsByPatient(@PathVariable Long patientId) {
        return appointmentsService.getActiveAppointmentsByPatient(patientId).stream()
                .map(AppointmentResponseDto::new)
                .collect(Collectors.toList());
    }
}

