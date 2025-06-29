package org.dariaob.models;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

/**
 * Сущность, представляющая медицинскую специализацию врача.
 * Соответствует таблице "specializations".
 */
@Entity
@Table(name = "specializations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Specializations implements Serializable {
    /**
     * Уникальный идентификатор специализации.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Название специализации.
     */
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    /**
     * Описание специализации.
     */
    private String description;

    /**
     * Флаг мягкого удаления специализации.
     */
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;
}
