package org.dariaob.dto.offices;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.dariaob.models.Offices;

@Getter
@Setter
@Schema(description = "Информация о кабинете")
public class OfficeDto {
    @Schema(description = "ID кабинета")
    private Long id;

    @Schema(description = "Название кабинета")
    private String name;

    public OfficeDto(Offices entity) {
        this.id = entity.getId();
        this.name = entity.getName();
    }

    public OfficeDto() {

    }
}
