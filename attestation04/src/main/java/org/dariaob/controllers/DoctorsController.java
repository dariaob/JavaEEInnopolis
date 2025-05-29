package org.dariaob.controllers;

import org.dariaob.dto.doctors.DoctorRequestDto;
import org.dariaob.dto.doctors.DoctorResponseDto;
import org.dariaob.models.*;
import org.dariaob.services.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Контроллер для управления врачами и их специализациями.
 */
@RestController
@RequestMapping("/api/v1/doctors")
@RequiredArgsConstructor
@Tag(name = "Врачи", description = "API для управления врачами и их специализациями")
public class DoctorsController {

    private final DoctorsService doctorsService;
    private final OfficesService officesService;
    private final SpecializationsService specializationsService;
    private final DoctorSpecializationsService doctorSpecializationsService;

    /**
     * Получить всех активных врачей.
     */
    @Operation(
            summary = "Получить всех активных врачей",
            description = "Возвращает список всех врачей, которые не были мягко удалены.",
            tags = {"Врачи"}
    )
    @GetMapping
    public List<DoctorResponseDto> getAllDoctors() {
        return doctorsService.getAllActive().stream()
                .map(DoctorResponseDto::new)
                .collect(Collectors.toList());
    }

    /**
     * Получить врача по его идентификатору.
     */
    @Operation(
            summary = "Получить врача по ID",
            description = "Возвращает информацию об активном враче по заданному ID.",
            tags = {"Врачи"}
    )
    @GetMapping("/{id}")
    public DoctorResponseDto getDoctorById(
            @Parameter(description = "ID врача") @PathVariable Long id) {
        return new DoctorResponseDto(doctorsService.getActiveById(id));
    }

    /**
     * Создать нового врача.
     */
    @Operation(
            summary = "Создать нового врача",
            description = "Создаёт нового врача и связывает его с кабинетом и специализациями.",
            tags = {"Врачи"}
    )
    @PostMapping
    public DoctorResponseDto createDoctor(
            @Parameter(description = "Данные врача") @RequestBody DoctorRequestDto dto) {

        Doctors doctor = dto.toEntity();
        Offices office = officesService.getActiveOfficeById(dto.getOfficeId());
        doctor.setOffice(office);
        Doctors savedDoctor = doctorsService.save(doctor);

        if (dto.getSpecializationIds() != null) {
            dto.getSpecializationIds().forEach(specId -> {
                Specializations spec = specializationsService.getActiveById(specId);
                DoctorSpecializations ds = new DoctorSpecializations();
                ds.setDoctor(savedDoctor);
                ds.setSpecialization(spec);
                doctorSpecializationsService.save(ds);
            });
        }

        return new DoctorResponseDto(savedDoctor);
    }

    /**
     * Обновить информацию о враче.
     */
    @Operation(
            summary = "Обновить данные врача",
            description = "Обновляет данные врача, включая кабинет и специализации.",
            tags = {"Врачи"}
    )
    @PutMapping("/{id}")
    public DoctorResponseDto updateDoctor(
            @Parameter(description = "ID врача") @PathVariable Long id,
            @Parameter(description = "Обновленные данные") @RequestBody DoctorRequestDto dto) {

        Doctors doctor = doctorsService.getActiveById(id);
        doctor.setName(dto.getName());
        doctor.setPhone(dto.getPhone());
        doctor.setWorkHoursFrom(dto.getWorkHoursFrom());
        doctor.setWorkHoursFor(dto.getWorkHoursFor());

        Offices office = officesService.getActiveOfficeById(dto.getOfficeId());
        doctor.setOffice(office);
        updateDoctorSpecializations(doctor, dto.getSpecializationIds());

        return new DoctorResponseDto(doctorsService.save(doctor));
    }

    /**
     * Мягко удалить врача.
     */
    @Operation(
            summary = "Удалить врача (soft delete)",
            description = "Мягко удаляет врача по ID — он больше не будет отображаться, но данные сохраняются.",
            tags = {"Врачи"}
    )
    @DeleteMapping("/{id}")
    public void deleteDoctor(
            @Parameter(description = "ID врача") @PathVariable Long id) {
        doctorsService.softDelete(id);
    }

    /**
     * Восстановить ранее удалённого врача.
     */
    @Operation(
            summary = "Восстановить врача",
            description = "Восстанавливает ранее мягко удалённого врача по ID.",
            tags = {"Врачи"}
    )
    @PostMapping("/restore/{id}")
    public void restoreDoctor(
            @Parameter(description = "ID врача") @PathVariable Long id) {
        doctorsService.restore(id);
    }

    /**
     * Получить список ID специализаций врача.
     */
    @Operation(
            summary = "Получить специализации врача",
            description = "Возвращает список ID специализаций, связанных с врачом.",
            tags = {"Врачи"}
    )
    @GetMapping("/{doctorId}/specializations")
    public List<Long> getDoctorSpecializations(
            @Parameter(description = "ID врача") @PathVariable Long doctorId) {
        return doctorSpecializationsService.getSpecializationsByDoctorId(doctorId).stream()
                .map(ds -> ds.getSpecialization().getId())
                .collect(Collectors.toList());
    }

    /**
     * Добавить специализацию врачу.
     */
    @Operation(
            summary = "Добавить специализацию врачу",
            description = "Добавляет новую специализацию врачу, если она ещё не привязана.",
            tags = {"Врачи"}
    )
    @PostMapping("/{doctorId}/specializations/{specializationId}")
    public void addSpecializationToDoctor(
            @Parameter(description = "ID врача") @PathVariable Long doctorId,
            @Parameter(description = "ID специализации") @PathVariable Long specializationId) {

        Doctors doctor = doctorsService.getActiveById(doctorId);
        Specializations spec = specializationsService.getActiveById(specializationId);

        if (!doctorSpecializationsService.existsByDoctorAndSpecialization(doctorId, specializationId)) {
            DoctorSpecializations ds = new DoctorSpecializations();
            ds.setDoctor(doctor);
            ds.setSpecialization(spec);
            doctorSpecializationsService.save(ds);
        }
    }

    /**
     * Удалить специализацию у врача.
     */
    @Operation(
            summary = "Удалить специализацию у врача",
            description = "Удаляет связь между врачом и указанной специализацией.",
            tags = {"Врачи"}
    )
    @DeleteMapping("/{doctorId}/specializations/{specializationId}")
    public void removeSpecializationFromDoctor(
            @Parameter(description = "ID врача") @PathVariable Long doctorId,
            @Parameter(description = "ID специализации") @PathVariable Long specializationId) {

        doctorSpecializationsService.deleteSpecialization(doctorId, specializationId);
    }

    /**
     * Обновляет специализации врача на основе новых данных.
     */
    private void updateDoctorSpecializations(Doctors doctor, Set<Long> newSpecializationIds) {
        List<DoctorSpecializations> currentSpecializations =
                doctorSpecializationsService.getSpecializationsByDoctorId(doctor.getId());

        currentSpecializations.stream()
                .filter(ds -> !newSpecializationIds.contains(ds.getSpecialization().getId()))
                .forEach(ds -> doctorSpecializationsService.deleteSpecialization(
                        doctor.getId(),
                        ds.getSpecialization().getId()));

        newSpecializationIds.forEach(specId -> {
            if (currentSpecializations.stream()
                    .noneMatch(ds -> ds.getSpecialization().getId().equals(specId))) {

                Specializations spec = specializationsService.getActiveById(specId);
                DoctorSpecializations ds = new DoctorSpecializations();
                ds.setDoctor(doctor);
                ds.setSpecialization(spec);
                doctorSpecializationsService.save(ds);
            }
        });
    }
}
