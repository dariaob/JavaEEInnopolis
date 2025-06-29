package org.dariaob.dto.specializations;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.dariaob.models.Specializations;

/**
 * DTO для представления информации о специализации врача.
 * Используется в API для отображения и передачи данных о специализациях.
 */
@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Информация о специализации врача")
public class SpecializationDto {

    /**
     * Уникальный идентификатор специализации.
     */
    @Schema(description = "ID специализации", example = "1")
    private Long id;

    /**
     * Название специализации (например, Терапевт, Хирург).
     */
    @Schema(description = "Название специализации", example = "Кардиолог")
    private String name;

    /**
     * Конструктор для преобразования сущности Specializations в DTO.
     *
     * @param entity объект Specializations из базы данных
     */
    public SpecializationDto(Specializations entity) {
        this.id = entity.getId();
        this.name = entity.getName();
    }
}
