package entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * Информация по врачу
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DoctorEntity {
    // id Врача
    private Long id;
    // ФИО врача
    private String name;
    // Начало времени приема
    private LocalDateTime workHoursFrom;
    // Время окончания приема
    private LocalDateTime workHoursFor;
    // Номер (id) кабинета
    private Long officeId;
}
