package org.dariaob.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Таблица: patient_cards_history
 * История изменений медицинских карт
 */
@Entity
@Table(name = "patient_cards_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientCardsHistory {
    // Уникальный идентификатор записи истории
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Ссылка на медкарту
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "card_id", nullable = false)
    private PatientCards card;

    // Дата и время изменения
    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt;

    // Кто внес изменения (логин пользователя)
    @Column(name = "changed_by", length = 50)
    private String changedBy;

    // Предыдущий диагноз
    @Column(name = "old_diagnosis", length = 255)
    private String oldDiagnosis;

    // Новый диагноз
    @Column(name = "new_diagnosis", nullable = false, length = 255)
    private String newDiagnosis;

    // Предыдущие назначения
    @Column(name = "old_meds")
    private String oldMeds;

    // Новые назначения
    @Column(name = "new_meds")
    private String newMeds;

    // Причина изменения
    @Column(name = "change_reason")
    private String changeReason;
}