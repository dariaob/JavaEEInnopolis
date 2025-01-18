package entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
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

    public DoctorEntity(Long id, String name, LocalDateTime workHoursFrom, LocalDateTime workHoursFor, Long officeId) {
        this.id = id;
        this.name = name;
        this.workHoursFrom = workHoursFrom;
        this.workHoursFor = workHoursFor;
        this.officeId = officeId;
    }
}
