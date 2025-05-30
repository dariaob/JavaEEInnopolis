package org.dariaob.dto.patientCards;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для запросов создания или обновления карты пациента.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO запроса для создания/обновления карты пациента")
public class PatientCardRequestDto {

    /**
     * Описание симптомов пациента.
     */
    @Schema(description = "Описание симптомов пациента")
    private String symptoms;

    /**
     * Диагноз пациента.
     */
    @Schema(description = "Диагноз пациента")
    private String diagnosis;

    /**
     * Назначенные лекарства.
     */
    @Schema(description = "Назначенные лекарства")
    private String meds;

    /**
     * Флаг, указывающий удалена ли карта.
     */
    @Schema(description = "Флаг удаления карты пациента", defaultValue = "false")
    private boolean isDeleted;
}
