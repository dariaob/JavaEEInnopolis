package org.dariaob.controllers;

import org.dariaob.dto.doctors.DoctorRequestDto;
import org.dariaob.dto.doctors.DoctorResponseDto;
import org.dariaob.exceptions.DataNotFoundException;
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

@RestController
@RequestMapping("/api/v1/doctors")
@RequiredArgsConstructor
@Tag(name = "Врачи", description = "API для управления врачами и их специализациями")
public class DoctorsController {

    private final DoctorsService doctorsService;
    private final OfficesService officesService;
    private final SpecializationsService specializationsService;
    private final DoctorSpecializationsService doctorSpecializationsService;

    @Operation(summary = "Получить всех активных врачей")
    @GetMapping
    public List<DoctorResponseDto> getAllDoctors() {
        return doctorsService.getAllActive().stream()
                .map(DoctorResponseDto::new)
                .collect(Collectors.toList());
    }

    @Operation(summary = "Получить врача по ID")
    @GetMapping("/{id}")
    public DoctorResponseDto getDoctorById(
            @Parameter(description = "ID врача") @PathVariable Long id) {
        return new DoctorResponseDto(doctorsService.getActiveById(id));
    }

    @Operation(summary = "Создать нового врача")
    @PostMapping
    public DoctorResponseDto createDoctor(
            @Parameter(description = "Данные врача") @RequestBody DoctorRequestDto dto) {

        Doctors doctor = dto.toEntity();

        // Устанавливаем кабинет
        Offices office = officesService.getActiveOfficeById(dto.getOfficeId());
        doctor.setOffice(office);

        // Сохраняем врача
        Doctors savedDoctor = doctorsService.save(doctor);

        // Добавляем специализации
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

    @Operation(summary = "Обновить данные врача")
    @PutMapping("/{id}")
    public DoctorResponseDto updateDoctor(
            @Parameter(description = "ID врача") @PathVariable Long id,
            @Parameter(description = "Обновленные данные") @RequestBody DoctorRequestDto dto) {

        Doctors doctor = doctorsService.getActiveById(id);

        // Обновляем основные данные
        doctor.setName(dto.getName());
        doctor.setPhone(dto.getPhone());
        doctor.setWorkHoursFrom(dto.getWorkHoursFrom());
        doctor.setWorkHoursFor(dto.getWorkHoursFor());

        // Обновляем кабинет
        Offices office = officesService.getActiveOfficeById(dto.getOfficeId());
        doctor.setOffice(office);

        // Обновляем специализации
        updateDoctorSpecializations(doctor, dto.getSpecializationIds());

        return new DoctorResponseDto(doctorsService.save(doctor));
    }

    @Operation(summary = "Удалить врача (soft delete)")
    @DeleteMapping("/{id}")
    public void deleteDoctor(
            @Parameter(description = "ID врача") @PathVariable Long id) {
        doctorsService.softDelete(id);
    }

    @Operation(summary = "Восстановить врача")
    @PostMapping("/restore/{id}")
    public void restoreDoctor(
            @Parameter(description = "ID врача") @PathVariable Long id) {
        doctorsService.restore(id);
    }

    @Operation(summary = "Получить специализации врача")
    @GetMapping("/{doctorId}/specializations")
    public List<Long> getDoctorSpecializations(
            @Parameter(description = "ID врача") @PathVariable Long doctorId) {
        return doctorSpecializationsService.getSpecializationsByDoctorId(doctorId).stream()
                .map(ds -> ds.getSpecialization().getId())
                .collect(Collectors.toList());
    }

    @Operation(summary = "Добавить специализацию врачу")
    @PostMapping("/{doctorId}/specializations/{specializationId}")
    public void addSpecializationToDoctor(
            @Parameter(description = "ID врача") @PathVariable Long doctorId,
            @Parameter(description = "ID специализации") @PathVariable Long specializationId) {

        Doctors doctor = doctorsService.getActiveById(doctorId);
        Specializations spec = specializationsService.getActiveById(specializationId);

        // Проверяем, нет ли уже такой связи
        if (!doctorSpecializationsService.existsByDoctorAndSpecialization(doctorId, specializationId)) {
            DoctorSpecializations ds = new DoctorSpecializations();
            ds.setDoctor(doctor);
            ds.setSpecialization(spec);
            doctorSpecializationsService.save(ds);
        }
    }

    @Operation(summary = "Удалить специализацию у врача")
    @DeleteMapping("/{doctorId}/specializations/{specializationId}")
    public void removeSpecializationFromDoctor(
            @Parameter(description = "ID врача") @PathVariable Long doctorId,
            @Parameter(description = "ID специализации") @PathVariable Long specializationId) {

        doctorSpecializationsService.deleteSpecialization(doctorId, specializationId);
    }


    private void updateDoctorSpecializations(Doctors doctor, Set<Long> newSpecializationIds) {
        // Получаем текущие специализации
        List<DoctorSpecializations> currentSpecializations =
                doctorSpecializationsService.getSpecializationsByDoctorId(doctor.getId());

        // Удаляем отсутствующие в новом списке
        currentSpecializations.stream()
                .filter(ds -> !newSpecializationIds.contains(ds.getSpecialization().getId()))
                .forEach(ds -> doctorSpecializationsService.deleteSpecialization(
                        doctor.getId(),
                        ds.getSpecialization().getId()));

        // Добавляем новые
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