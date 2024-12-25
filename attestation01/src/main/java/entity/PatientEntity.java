package entity;

import lombok.*;

/**
 * Данные о пациенте
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PatientEntity {
    // Уникальный идентификатор для пациента
    private Long patientId;
    // Номер страхования
    private Long insuranceId;
    // ФИО
    private String name;
    // Адрес
    private String address;
    // Номер карты пациента
    private Long cardId;
}
