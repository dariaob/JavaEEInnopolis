package entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * Прием
 */
@Data
@NoArgsConstructor
@ToString
public class ReceptionEntity {
    // Номер(id) приёма
    private Long id;
    // Номер (id) врача
    private Long doctorId;
    // Номер кабинета
    private Long officeId;
    // Начало времени приема
    private LocalDateTime workHoursFrom;
    // Время окончания приема
    private LocalDateTime workHoursFor;
    // Номер карты пациента
    private Long cardId;
    // Id пациента
    private Long patientId;
    // Страховой номер пациента
    private Long insuranceId;

    // Конструктор для всех полей
    public ReceptionEntity(Long id, Long doctorId, Long officeId, LocalDateTime workHoursFrom, LocalDateTime workHoursFor, Long cardId, Long patientId, Long insuranceId) {
        this.id = id;
        this.doctorId = doctorId;
        this.officeId = officeId;
        this.workHoursFrom = workHoursFrom;
        this.workHoursFor = workHoursFor;
        this.cardId = cardId;
        this.patientId = patientId;
        this.insuranceId = insuranceId;
    }
}
