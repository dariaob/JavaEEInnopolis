package org.dariaob.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Таблица: specializations
 * Медицинские специализации врачей
 */
@Entity
@Table(name = "specializations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Specializations {
    // Уникальный идентификатор специализации
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Название специализации
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    // Описание специализации
    private String description;

    // Флаг удаления
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;
}
