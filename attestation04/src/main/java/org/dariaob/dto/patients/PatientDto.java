package org.dariaob.dto.patients;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO для передачи данных о пациенте между слоями приложения.
 * Используется в ответах и запросах API, представляет базовую информацию о пациенте.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO объекта пациента клиники")
public class PatientDto {

    /**
     * Уникальный идентификатор пациента.
     * Только для чтения — не должен задаваться вручную при создании.
     */
    @Schema(description = "Уникальный идентификатор пациента", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    /**
     * Полное имя пациента.
     */
    @Schema(description = "Полное имя пациента", maxLength = 30)
    private String name;

    /**
     * Дата рождения пациента.
     */
    @Schema(description = "Дата рождения пациента")
    private LocalDate birthDate;

    /**
     * Контактный телефон пациента.
     */
    @Schema(description = "Контактный телефон пациента", maxLength = 20)
    private String phone;

    /**
     * Идентификатор привязанной медицинской карты пациента.
     */
    @Schema(description = "Идентификатор привязанной медицинской карты")
    private Long patientCardId;

    /**
     * Флаг, указывающий, удалён ли пациент (мягкое удаление).
     */
    @Schema(description = "Флаг удаления пациента")
    private boolean isDeleted;

    /**
     * Идентификатор страхового полиса пациента.
     */
    @Schema(description = "Номер страхового полиса пациента")
    private Long insuranceId;
}
