package org.dariaob.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dariaob.dto.appointments.AppointmentResponseDto;
import org.dariaob.services.AppointmentsService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Контроллер для управления приёмами (Appointments).
 */
@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
@Tag(name = "Приёмы", description = "Операции для управления приёмами пациентов")
public class AppointmentsController {

    /**
     * Сервис приема
     */
    private final AppointmentsService appointmentsService;

    /**
     * Получить список всех активных приемов
     * @return List приемов
     */
    @Operation(
            summary = "Получить список всех активных приёмов",
            description = "Возвращает список всех активных приёмов с информацией о врачах и пациентах.",
            tags = {"Приёмы"}
    )
    @GetMapping("/active")
    public List<AppointmentResponseDto> getAllActiveAppointments() {
        return appointmentsService.getAllActiveAppointments().stream()
                .map(AppointmentResponseDto::new)
                .collect(Collectors.toList());
    }

    /**
     * Получить активный прием по id
     * @param id Идентификатор приема
     * @return прием
     */
    @Operation(
            summary = "Получить активный приём по ID",
            description = "Возвращает информацию об активном приёме по его идентификатору.",
            tags = {"Приёмы"}
    )
    @GetMapping("/{id}")
    public AppointmentResponseDto getActiveAppointmentById(@PathVariable Long id) {
        return new AppointmentResponseDto(appointmentsService.getActiveAppointmentById(id));
    }

    /**
     * Удаление приема через флаг isDeleted
     * @param id идентификатор приема
     */
    @Operation(
            summary = "Мягко удалить приём по ID",
            description = "Помечает приём как удалённый без физического удаления из базы данных.",
            tags = {"Приёмы"}
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void softDeleteAppointment(@PathVariable Long id) {
        appointmentsService.softDeleteAppointment(id);
    }

    /**
     * Восстановить приема через флаг isDeleted = false
     * @param id прием
     */
    @Operation(
            summary = "Восстановить мягко удалённый приём",
            description = "Восстанавливает ранее мягко удалённый приём по его идентификатору.",
            tags = {"Приёмы"}
    )
    @PutMapping("/{id}/restore")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void restoreAppointment(@PathVariable Long id) {
        appointmentsService.restoreAppointment(id);
    }

    /**
     * Получить активные приемы по врачу
     * @param doctorId идентификатор врача
     * @return массив приемов
     */
    @Operation(
            summary = "Получить активные приёмы по врачу",
            description = "Возвращает все активные приёмы, назначенные определённому врачу.",
            tags = {"Приёмы"}
    )
    @GetMapping("/doctor/{doctorId}")
    public List<AppointmentResponseDto> getActiveAppointmentsByDoctor(@PathVariable Long doctorId) {
        return appointmentsService.getActiveAppointmentsByDoctor(doctorId).stream()
                .map(AppointmentResponseDto::new)
                .collect(Collectors.toList());
    }

    /**
     * Получить активные приёмы по пациенту
     * @param patientId идентификатор пациента
     * @return массив приема
     */
    @Operation(
            summary = "Получить активные приёмы по пациенту",
            description = "Возвращает все активные приёмы, связанные с конкретным пациентом.",
            tags = {"Приёмы"}
    )
    @GetMapping("/patient/{patientId}")
    public List<AppointmentResponseDto> getActiveAppointmentsByPatient(@PathVariable Long patientId) {
        return appointmentsService.getActiveAppointmentsByPatient(patientId).stream()
                .map(AppointmentResponseDto::new)
                .collect(Collectors.toList());
    }
}
