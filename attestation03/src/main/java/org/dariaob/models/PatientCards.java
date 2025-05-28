package org.dariaob.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Таблица: patient_cards
 * Медицинские карты пациентов
 */
@Entity
@Table(name = "patient_cards")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientCards {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Уникальный идентификатор карты

    @Column(nullable = false, length = 255)
    private String symptoms; // Описание симптомов

    @Column(nullable = false, length = 255)
    private String diagnosis; // Поставленный диагноз

    @Column(length = 255)
    private String meds; // Назначенные лекарства (может быть null)

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted; // Флаг удаления

    // Добавляем обратную связь OneToOne
    @OneToOne(mappedBy = "patientCard", fetch = FetchType.LAZY)
    private Patients patient;
}

