package org.dariaob.dto.users;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * DTO для представления информации о пользователе.
 */
@Data
@Schema(description = "Данные о пользователе")
public class UserDto {

    /**
     * Имя пользователя.
     */
    @Schema(description = "Имя пользователя")
    private String username;

    /**
     * Пароль пользователя
     */
    @Schema(description = "Пароль пользователя")
    private String password;

    /**
     * Флаг, показывающий, был ли пользователь удален.
     */
    @Schema(description = "Флаг, показывающий, был ли пользователь удален")
    private boolean isDeleted;
}

