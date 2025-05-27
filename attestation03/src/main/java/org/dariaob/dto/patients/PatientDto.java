package org.dariaob.dto.patients;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO для сущности Patients
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO объекта пациента клиники")
public class PatientDto {
    @Schema(description = "Уникальный идентификатор пациента", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Полное имя пациента", maxLength = 30)
    private String name;

    @Schema(description = "Дата рождения пациента")
    private LocalDate birthDate;

    @Schema(description = "Контактный телефон пациента", maxLength = 20)
    private String phone;

    @Schema(description = "Идентификатор привязанной медицинской карты")
    private Long patientCardId;

    @Schema(description = "Флаг удаления пациента")
    private boolean isDeleted;

    @Schema(description = "Номер страхового полиса пациента")
    private Long insuranceId;
}
