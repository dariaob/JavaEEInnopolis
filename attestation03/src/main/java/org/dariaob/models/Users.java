package org.dariaob.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Array;

import java.util.List;

/**
 * Класс - сущность объектов таблицы users
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Users {

    /**
     * Логин
     */
    @Id
    @Column(name = "username", length = 30)
    private String username;

    /**
     * Пароль
     */
    @Column(name = "password", nullable = false)
    private String password;

    /**
     * Роли
     */
    @Array(length = 50)
    @Column(name = "roles", columnDefinition = "text[]")
    private List<String> roles;

    /**
     * Флаг для Soft Delete
     */
    private boolean isDeleted = false;
}
