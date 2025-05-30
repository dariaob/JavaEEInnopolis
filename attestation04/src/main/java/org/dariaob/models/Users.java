package org.dariaob.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Array;

import java.util.Arrays;
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
    @Column(name = "roles", nullable = false)
    private String roles;

    /**
     * Флаг для Soft Delete
     */
    private boolean isDeleted = false;

    public List<String> getRoles() {
        return Arrays.asList(roles.split(","));
    }

    public void setRoleLs(List<String> roleList) {
        this.roles = String.join(",", roleList);
    }
}
