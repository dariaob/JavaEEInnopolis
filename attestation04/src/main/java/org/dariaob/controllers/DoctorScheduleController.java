package org.dariaob.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dariaob.dto.doctorSchedule.DoctorScheduleDto;
import org.dariaob.services.DoctorScheduleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;


import org.dariaob.models.DoctorSchedule;

import java.util.stream.Collectors;

/**
 * Контроллер для управления расписанием врачей.
 * Предоставляет API для работы с расписанием приёма врачей.
 */
@RestController
@RequestMapping("/api/v1/doctor-schedules")
@RequiredArgsConstructor
@Tag(name = "Расписание врачей", description = "API для взаимодействия с расписанием врачей")
public class DoctorScheduleController {

    /**
     * Сервис для работы с расписанием врачей
     */
    private final DoctorScheduleService doctorScheduleService;

    /**
     * Получить все активные (неудалённые) записи расписания врачей.
     *
     * @return список всех активных расписаний в формате DTO
     */
    @Operation(
            summary = "Получить все активные расписания",
            description = "Возвращает список всех активных (неудалённых) расписаний врачей",
            tags = {"Расписание врачей"}
    )
    @GetMapping
    public List<DoctorScheduleDto> getAllActive() {
        return doctorScheduleService.getAllActive().stream()
                .map(DoctorScheduleDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Получить активное расписание по идентификатору.
     *
     * @param id идентификатор расписания
     * @return расписание в формате DTO
     */
    @Operation(
            summary = "Получить расписание по ID",
            description = "Возвращает активное расписание врача по указанному идентификатору",
            tags = {"Расписание врачей"}
    )
    @GetMapping("/{id}")
    public DoctorScheduleDto getActiveById(
            @Parameter(description = "Идентификатор расписания")
            @PathVariable Long id) {
        return doctorScheduleService.getActiveById(id)
                .map(DoctorScheduleDto::fromEntity)
                .orElseThrow(() -> new RuntimeException("Расписание не найдено"));
    }

    /**
     * Получить расписание по врачу.
     *
     * @param doctorId идентификатор врача
     * @return список расписаний в формате DTO
     */
    @Operation(
            summary = "Получить расписание по врачу",
            description = "Возвращает расписание для указанного врача",
            tags = {"Расписание врачей"}
    )
    @GetMapping("/by-doctor/{doctorId}")
    public List<DoctorScheduleDto> getByDoctor(
            @Parameter(description = "Идентификатор врача")
            @PathVariable Long doctorId) {
        return doctorScheduleService.getByDoctor(doctorId).stream()
                .map(DoctorScheduleDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Получить расписание врача по дню недели.
     *
     * @param doctorId  идентификатор врача
     * @param dayOfWeek день недели (1-7)
     * @return список расписаний в формате DTO
     */
    @Operation(
            summary = "Получить расписание врача по дню недели",
            description = "Возвращает расписание для указанного врача в указанный день недели",
            tags = {"Расписание врачей"}
    )
    @GetMapping("/by-doctor-day/{doctorId}/{dayOfWeek}")
    public List<DoctorScheduleDto> getByDoctorAndDay(
            @Parameter(description = "Идентификатор врача")
            @PathVariable Long doctorId,
            @Parameter(description = "День недели (1-7)")
            @PathVariable Short dayOfWeek) {
        return doctorScheduleService.getByDoctorAndDay(doctorId, dayOfWeek).stream()
                .map(DoctorScheduleDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Создать новую запись в расписании врача.
     *
     * @param doctorScheduleDto DTO с данными расписания
     * @return созданное расписание в формате DTO
     */
    @Operation(
            summary = "Создать запись в расписании",
            description = "Создаёт новую запись в расписании врача",
            tags = {"Расписание врачей"}
    )
    @PostMapping
    public DoctorScheduleDto create(
            @Parameter(description = "Данные расписания")
            @RequestBody DoctorScheduleDto doctorScheduleDto) {
        DoctorSchedule schedule = doctorScheduleService.create(doctorScheduleDto.toEntity());
        return DoctorScheduleDto.fromEntity(schedule);
    }

    /**
     * Обновить запись в расписании врача.
     *
     * @param id                идентификатор расписания
     * @param doctorScheduleDto DTO с обновлёнными данными
     * @return обновлённое расписание в формате DTO
     */
    @Operation(
            summary = "Обновить запись в расписании",
            description = "Обновляет существующую запись в расписании врача",
            tags = {"Расписание врачей"}
    )
    @PutMapping("/{id}")
    public DoctorScheduleDto update(
            @Parameter(description = "Идентификатор расписания")
            @PathVariable Long id,
            @Parameter(description = "Обновлённые данные расписания")
            @RequestBody DoctorScheduleDto doctorScheduleDto) {
        DoctorSchedule updatedSchedule = doctorScheduleDto.toEntity();
        DoctorSchedule schedule = doctorScheduleService.update(id, updatedSchedule);
        return DoctorScheduleDto.fromEntity(schedule);
    }

    /**
     * Пометить запись в расписании как удалённую (soft delete).
     *
     * @param id идентификатор расписания
     */
    @Operation(
            summary = "Удалить запись в расписании",
            description = "Помечает запись в расписании как удалённую (soft delete)",
            tags = {"Расписание врачей"}
    )
    @DeleteMapping("/{id}")
    public void delete(
            @Parameter(description = "Идентификатор расписания")
            @PathVariable Long id) {
        doctorScheduleService.delete(id);
    }

    /**
     * Восстановить удалённую запись в расписании.
     *
     * @param id идентификатор расписания
     */
    @Operation(
            summary = "Восстановить запись в расписании",
            description = "Восстанавливает ранее удалённую запись в расписании",
            tags = {"Расписание врачей"}
    )
    @PostMapping("/restore/{id}")
    public void restore(
            @Parameter(description = "Идентификатор расписания")
            @PathVariable Long id) {
        doctorScheduleService.restore(id);
    }
}
