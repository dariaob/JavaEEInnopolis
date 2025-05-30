package org.dariaob.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * Сущность "Запись на приём".
 * Хранит данные о приёме пациента у врача, включая дату, врача, пациента, кабинет и другие детали.
 */
@Entity
@Table(name = "appointments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Appointments {

    /**
     * Уникальный идентификатор записи на приём.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Дата и время приёма.
     */
    @Column(nullable = false)
    private LocalDateTime date;

    /**
     * Врач, к которому записан пациент.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctors doctor;

    /**
     * Пациент, записанный на приём.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patients patient;

    /**
     * Планируемое время начала приёма.
     */
    @Column(name = "work_hours_from", nullable = false)
    private LocalDateTime workHoursFrom;

    /**
     * Планируемое время окончания приёма.
     */
    @Column(name = "work_hours_for", nullable = false)
    private LocalDateTime workHoursFor;

    /**
     * Флаг, указывающий на мягкое удаление записи.
     */
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    /**
     * Медицинская карта, связанная с приёмом.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "card_id", nullable = false)
    private PatientCards card;

    /**
     * Номер страхового полиса, использованный при записи.
     */
    @Column(name = "insurance_id", nullable = false)
    private Long insuranceId;

    /**
     * Кабинет, в котором состоится приём.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "office_id", nullable = false)
    private Offices office;
}
