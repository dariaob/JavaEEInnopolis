package org.dariaob.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

/**
 * Entity-класс для хранения расписания врача.
 */
@Entity
@Table(name = "doctor_schedule")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Сущность расписания врача")
public class DoctorSchedule {

    /**
     * Уникальный идентификатор записи расписания.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Идентификатор расписания")
    private Long id;

    /**
     * Врач, к которому относится расписание.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    @Schema(description = "Врач, связанный с расписанием")
    private Doctors doctor;

    /**
     * День недели
     */
    @Column(name = "day_of_week", nullable = false)
    @Schema(description = "День недели")
    private Short dayOfWeek;

    /**
     * Время начала приема.
     */
    @Column(name = "start_time", nullable = false)
    @Schema(description = "Время начала приёма")
    private LocalTime startTime;

    /**
     * Время окончания приема.
     */
    @Column(name = "end_time", nullable = false)
    @Schema(description = "Время окончания приёма")
    private LocalTime endTime;

    /**
     * Кабинет, в котором проходит прием.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "office_id")
    @Schema(description = "Кабинет, в котором проходит приём")
    private Offices office;

    @Column(name = "is_deleted", nullable = false)
    @Schema(description = "Признак удаления")
    private boolean isDeleted = false;

}

