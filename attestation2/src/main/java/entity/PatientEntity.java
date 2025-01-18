package entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * ПДанные пациента
 */
@Data
@NoArgsConstructor
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

    public PatientEntity(Long patientId, Long insuranceId, String name, String address, Long cardId) {
        this.patientId = patientId;
        this.insuranceId = insuranceId;
        this.name = name;
        this.address = address;
        this.cardId = cardId;
    }
}
