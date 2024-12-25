package entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
