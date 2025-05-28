package org.dariaob.models;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

/**
 * Таблица: patients
 * Пациенты клиники
 */

@Entity
@Table(name = "patients")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Patients {
    // Уникальный идентификатор пациента
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Полное имя пациента
    @Column(nullable = false, length = 30)
    private String name;

    // Дата рождения
    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    // Контактный телефон
    @Column(nullable = false, length = 20, unique = true)
    private String phone;

    // Привязанная медицинская карта
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_card_id", nullable = false)
    private PatientCards patientCard;

    // Флаг удаления
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    // Номер страхового полиса
    @Column(name = "insurance_id", nullable = false)
    private Long insuranceId;
}