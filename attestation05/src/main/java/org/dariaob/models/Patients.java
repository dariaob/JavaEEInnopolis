package org.dariaob.models;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Сущность, представляющая пациента клиники.
 * Соответствует таблице "patients".
 */
@Entity
@Table(name = "patients")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Patients implements Serializable {
    /**
     * Уникальный идентификатор пациента.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Полное имя пациента.
     */
    @Column(nullable = false, length = 30)
    private String name;

    /**
     * Дата рождения пациента.
     */
    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    /**
     * Контактный телефон пациента.
     */
    @Column(nullable = false, length = 20, unique = true)
    private String phone;

    /**
     * Привязанная медицинская карта пациента.
     */
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_card_id", nullable = false)
    @ToString.Exclude
    private PatientCards patientCard;

    /**
     * Флаг мягкого удаления пациента.
     */
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    /**
     * Номер страхового полиса пациента.
     */
    @Column(name = "insurance_id", nullable = false)
    private Long insuranceId;
}
