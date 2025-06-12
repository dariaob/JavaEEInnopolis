package org.dariaob.dto.offices;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.dariaob.models.Offices;

/**
 * DTO для представления информации о кабинете.
 * Используется в ответах API и для отображения связанных данных.
 */
@Getter
@Setter
@Schema(description = "Информация о кабинете")
public class OfficeDto {

    /**
     * Уникальный идентификатор кабинета.
     */
    @Schema(description = "ID кабинета")
    private Long id;

    /**
     * Название кабинета (например, 'Кабинет №12').
     */
    @Schema(description = "Название кабинета")
    private String name;

    /**
     * Конструктор для преобразования сущности {@link Offices} в DTO.
     *
     * @param entity объект кабинета из базы данных
     */
    public OfficeDto(Offices entity) {
        this.id = entity.getId();
        this.name = entity.getName();
    }

    /**
     * Конструктор по умолчанию.
     * Используется при десериализации и ручном заполнении DTO.
     */
    public OfficeDto() {
        // Пустой конструктор
    }
}
