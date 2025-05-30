package org.dariaob.dto.patientCardsHistory;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * DTO для отображения истории изменений медицинской карты пациента.
 * Содержит информацию о том, кто и когда изменил данные карты,
 * а также предыдущие и новые значения полей.
 */
@Data
@Schema(description = "Запись об изменении медицинской карты")
public class PatientCardHistoryDto {

    /**
     * Уникальный идентификатор записи в истории изменений.
     * Генерируется автоматически при сохранении в базу данных.
     */
    @Schema(description = "ID записи истории")
    private Long id;

    /**
     * Дата и время внесения изменений в формате ISO-8601 (yyyy-MM-dd'T'HH:mm:ss).
     * Заполняется автоматически на сервере при создании записи.
     */
    @Schema(description = "Дата и время изменения")
    private LocalDateTime changedAt;

    /**
     * Логин пользователя (врача или администратора), внесшего изменения.
     * Если изменение выполнено системой автоматически, может содержать null.
     */
    @Schema(description = "Кто внес изменения (логин пользователя)")
    private String changedBy;

    /**
     * Диагноз, установленный после изменения.
     * Всегда обязателен при изменении диагноза.
     */
    @Schema(description = "Новый диагноз")
    private String newDiagnosis;

    /**
     * Предыдущий диагноз до изменения.
     * Может быть null, если диагноз устанавливался впервые.
     */
    @Schema(description = "Предыдущий диагноз")
    private String oldDiagnosis;

    /**
     * Назначенные лекарства после изменения.
     * Может быть null, если лекарства не назначались.
     */
    @Schema(description = "Новые назначенные лекарства")
    private String newMeds;

    /**
     * Назначенные лекарства до изменения.
     * Может быть null, если лекарства не назначались ранее.
     */
    @Schema(description = "Предыдущие назначенные лекарства")
    private String oldMeds;
}