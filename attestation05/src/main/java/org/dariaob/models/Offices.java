package org.dariaob.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * Сущность "Кабинет".
 * Хранит информацию о кабинетах приёма пациентов.
 */
@Entity
@Table(name = "offices")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Offices implements Serializable {

    /**
     * Уникальный идентификатор кабинета.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Название кабинета.
     */
    @Column(nullable = false)
    private String name;

    /**
     * Флаг удаления (true — удалён, false — активен).
     */
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;
}
