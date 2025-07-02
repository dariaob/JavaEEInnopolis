package org.dariaob.models;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Сущность для таблицы doctor_schedule_slots.
 * Представляет отдельный слот в расписании врача.
 * Слот содержит время начала и окончания,
 * а также статус бронирования и удаления (soft delete).
 */
@Entity
@Table(name = "doctor_schedule_slots")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "doctorSchedule")
public class DoctorScheduleSlot implements Serializable {

    /**
     * Идентификатор слота (первичный ключ).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Расписание врача, к которому относится слот.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_schedule_id", nullable = false)
    private DoctorSchedule doctorSchedule;

    /**
     * Время начала слота (включительно).
     */
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    /**
     * Время окончания слота (исключительно).
     */
    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    /**
     * Признак, что слот занят (забронирован).
     */
    @Column(name = "is_booked", nullable = false)
    private boolean isBooked = false;

    /**
     * Признак soft delete (логическое удаление).
     */
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    /**
     *  Прием
     */
    @OneToOne
    @JoinColumn(name = "appointment_id")
    private Appointments appointment;
}

