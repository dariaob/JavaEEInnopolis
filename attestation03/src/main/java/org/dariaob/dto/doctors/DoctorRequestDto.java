package org.dariaob.dto.doctors;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.dariaob.models.Doctors;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Schema(description = "Данные для создания/обновления врача")
public class DoctorRequestDto {

    @Schema(description = "ФИО врача")
    private String name;

    @Schema(description = "Контактный телефон")
    private String phone;

    @Schema(description = "Время начала рабочего дня")
    private LocalDateTime workHoursFrom;

    @Schema(description = "Время окончания рабочего дня")
    private LocalDateTime workHoursFor;

    @Schema(description = "ID кабинета")
    private Long officeId;

    @Schema(description = "Список ID специализаций")
    private Set<Long> specializationIds;

    // Метод для преобразования в Entity
    public Doctors toEntity() {
        Doctors doctor = new Doctors();
        doctor.setName(this.name);
        doctor.setPhone(this.phone);
        doctor.setWorkHoursFrom(this.workHoursFrom);
        doctor.setWorkHoursFor(this.workHoursFor);
        doctor.setDeleted(false); // По умолчанию не удален
        return doctor;
    }
}