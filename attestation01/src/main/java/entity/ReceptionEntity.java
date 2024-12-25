package entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.time.LocalDateTime;

/**
 *  Данные о приёме
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
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
}
