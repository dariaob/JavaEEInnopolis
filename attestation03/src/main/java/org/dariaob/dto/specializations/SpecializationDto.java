package org.dariaob.dto.specializations;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.dariaob.models.Specializations;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Информация о специализации врача")
public class SpecializationDto {
    @Schema(description = "ID специализации")
    private Long id;

    @Schema(description = "Название специализации")
    private String name;

    public SpecializationDto(Specializations entity) {
        this.id = entity.getId();
        this.name = entity.getName();
    }
}
