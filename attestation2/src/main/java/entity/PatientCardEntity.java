package entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Карточка пациента
 */
@NoArgsConstructor
@Data
@ToString
public class PatientCardEntity {
    // Номер(id) карточки пациента
    private Long id;
    // Жалобы, симптомы
    private String symptoms;
    // Диагноз
    private String diagnosis;
    // Лекарства
    private String medicine;

    public PatientCardEntity(Long id, String symptoms, String diagnosis, String medicine) {
        this.id = id;
        this.symptoms = symptoms;
        this.diagnosis = diagnosis;
        this.medicine = medicine;
    }
}
