package org.dariaob.dto.patientCards;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.dariaob.models.PatientCards;

/**
 * DTO для ответа с данными карты пациента.
 * Содержит полную информацию о медицинской карте, включая системные поля.
 * Используется для возврата данных клиенту при запросах информации о карте.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO ответа с данными карты пациента")
public class PatientCardResponseDto {

    /**
     * Уникальный идентификатор карты пациента в системе.
     * Только для чтения, генерируется автоматически.
     */
    @Schema(description = "Уникальный идентификатор карты пациента", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    /**
     * Текущие симптомы пациента.
     * Максимальная длина - 255 символов.
     */
    @Schema(description = "Описание симптомов пациента", maxLength = 255)
    private String symptoms;

    /**
     * Текущий диагноз пациента.
     * Максимальная длина - 255 символов.
     */
    @Schema(description = "Диагноз пациента", maxLength = 255)
    private String diagnosis;

    /**
     * Текущие назначенные лекарства.
     * Может быть null, если лекарства не назначены.
     * Максимальная длина - 255 символов.
     */
    @Schema(description = "Назначенные лекарства (может быть null)", maxLength = 255)
    private String meds;
}
