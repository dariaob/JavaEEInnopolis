package org.dariaob.controllers;

import org.dariaob.dto.patients.PatientDto;
import org.dariaob.exceptions.DataNotFoundException;
import org.dariaob.models.Patients;
import org.dariaob.models.PatientCards;
import org.dariaob.services.PatientsService;
import org.dariaob.services.PatientCardsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Контроллер для управления пациентами медицинского учреждения.
 */
@RestController
@RequestMapping("/api/v1/patients")
@RequiredArgsConstructor
@Tag(name = "Пациенты", description = "API для управления данными пациентов")
public class PatientsController {

    private final PatientsService patientsService;
    private final PatientCardsService patientCardsService;

    /**
     * Получить список всех активных (неудаленных) пациентов
     * @return список пациентов в формате DTO
     */
    @Operation(
            summary = "Получить всех активных пациентов",
            description = "Возвращает список всех пациентов, не помеченных как удаленные",
            tags = {"Пациенты"}
    )
    @GetMapping
    public List<PatientDto> getAllActivePatients() {
        return patientsService.getAllActive().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Получить данные пациента по ID
     * @param id идентификатор пациента
     * @return данные пациента в формате DTO
     * @throws DataNotFoundException если пациент не найден или удален
     */
    @Operation(
            summary = "Получить пациента по ID",
            description = "Возвращает полные данные пациента по указанному идентификатору",
            tags = {"Пациенты"}
    )
    @GetMapping("/{id}")
    public PatientDto getPatientById(
            @Parameter(description = "Уникальный идентификатор пациента", required = true)
            @PathVariable Long id) {
        return convertToDto(patientsService.getActiveById(id));
    }

    /**
     * Найти пациента по номеру телефона
     * @param phone номер телефона пациента
     * @return данные пациента в формате DTO
     * @throws DataNotFoundException если пациент не найден или удален
     */
    @Operation(
            summary = "Найти пациента по телефону",
            description = "Поиск пациента по точному совпадению номера телефона",
            tags = {"Пациенты"}
    )
    @GetMapping("/by-phone/{phone}")
    public PatientDto getPatientByPhone(
            @Parameter(description = "Номер телефона в международном формате", required = true)
            @PathVariable String phone) {
        return convertToDto(patientsService.getActiveByPhone(phone));
    }

    /**
     * Создать нового пациента в системе
     * @param patientDto DTO с данными для создания пациента
     * @return созданный пациент в формате DTO
     * @throws DataNotFoundException если привязанная медкарта не найдена
     */
    @Operation(
            summary = "Создать нового пациента",
            description = "Создает новую запись пациента с привязкой к медицинской карте",
            tags = {"Пациенты"}
    )
    @PostMapping
    public PatientDto createPatient(
            @Parameter(description = "Данные для создания пациента", required = true)
            @RequestBody PatientDto patientDto) {
        Patients patient = new Patients();
        patient.setName(patientDto.getName());
        patient.setBirthDate(patientDto.getBirthDate());
        patient.setPhone(patientDto.getPhone());
        patient.setInsuranceId(patientDto.getInsuranceId());
        patient.setDeleted(false);

        // Привязываем медицинскую карту с проверкой ее существования
        PatientCards card = patientCardsService.getActiveById(patientDto.getPatientCardId());
        patient.setPatientCard(card);

        Patients savedPatient = patientsService.save(patient);
        return convertToDto(savedPatient);
    }

    /**
     * Обновить данные существующего пациента
     * @param id идентификатор пациента
     * @param patientDto обновленные данные пациента
     * @return обновленный пациент в формате DTO
     * @throws DataNotFoundException если пациент или медкарта не найдены
     */
    @Operation(
            summary = "Обновить данные пациента",
            description = "Обновляет информацию о пациенте и его медкарте",
            tags = {"Пациенты"}
    )
    @PutMapping("/{id}")
    public PatientDto updatePatient(
            @Parameter(description = "ID обновляемого пациента", required = true)
            @PathVariable Long id,
            @Parameter(description = "Обновленные данные пациента", required = true)
            @RequestBody PatientDto patientDto) {
        Patients patient = patientsService.getActiveById(id);

        // Обновляем основные данные
        patient.setName(patientDto.getName());
        patient.setBirthDate(patientDto.getBirthDate());
        patient.setPhone(patientDto.getPhone());
        patient.setInsuranceId(patientDto.getInsuranceId());

        // Обновляем привязку к медкарте если изменился ID
        if (!patient.getPatientCard().getId().equals(patientDto.getPatientCardId())) {
            PatientCards card = patientCardsService.getActiveById(patientDto.getPatientCardId());
            patient.setPatientCard(card);
        }

        Patients updatedPatient = patientsService.save(patient);
        return convertToDto(updatedPatient);
    }

    /**
     * Пометить пациента как удаленного (soft delete)
     * @param id идентификатор пациента
     * @throws DataNotFoundException если пациент не найден или уже удален
     */
    @Operation(
            summary = "Удалить пациента",
            description = "Помечает пациента как удаленного без физического удаления данных",
            tags = {"Пациенты"}
    )
    @DeleteMapping("/{id}")
    public void deletePatient(
            @Parameter(description = "ID удаляемого пациента", required = true)
            @PathVariable Long id) {
        patientsService.softDelete(id);
    }

    /**
     * Восстановить ранее удаленного пациента
     * @param id идентификатор пациента
     * @throws DataNotFoundException если пациент не найден или уже активен
     */
    @Operation(
            summary = "Восстановить пациента",
            description = "Снимает отметку об удалении с пациента",
            tags = {"Пациенты"}
    )
    @PostMapping("/restore/{id}")
    public void restorePatient(
            @Parameter(description = "ID восстанавливаемого пациента", required = true)
            @PathVariable Long id) {
        patientsService.restore(id);
    }

    /**
     * Преобразует сущность пациента в DTO
     * @param patient сущность пациента
     * @return DTO пациента
     */
    private PatientDto convertToDto(Patients patient) {
        PatientDto dto = new PatientDto();
        dto.setId(patient.getId());
        dto.setName(patient.getName());
        dto.setBirthDate(patient.getBirthDate());
        dto.setPhone(patient.getPhone());
        dto.setPatientCardId(patient.getPatientCard().getId());
        dto.setDeleted(patient.isDeleted());
        dto.setInsuranceId(patient.getInsuranceId());
        return dto;
    }
}