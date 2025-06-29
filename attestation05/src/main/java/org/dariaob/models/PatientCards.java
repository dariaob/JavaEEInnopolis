package org.dariaob.models;

import jakarta.persistence.*;
import lombok.*;
import org.dariaob.models.Patients;

import java.io.Serializable;

/**
 * Сущность "Медицинская карта".
 * Представляет собой записи о симптомах, диагнозах и назначениях пациента.
 */
@Entity
@Table(name = "patient_cards")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientCards implements Serializable {

    /**
     * Уникальный идентификатор карты.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Описание симптомов пациента.
     */
    @Column(nullable = false, length = 255)
    private String symptoms;

    /**
     * Поставленный диагноз.
     */
    @Column(nullable = false, length = 255)
    private String diagnosis;

    /**
     * Назначенные лекарства (может быть null).
     */
    @Column(length = 255)
    private String meds;

    /**
     * Флаг удаления карты.
     */
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    /**
     * Связь с пациентом (обратная сторона OneToOne).
     */
    @OneToOne(mappedBy = "patientCard")
    private Patients patient;
}
