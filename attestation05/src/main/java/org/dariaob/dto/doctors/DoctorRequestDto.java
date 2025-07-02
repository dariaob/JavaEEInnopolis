package org.dariaob.dto.doctors;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.dariaob.models.Doctors;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Dto для врачей
 */
@Getter
@Setter
@Schema(description = "Данные для создания/обновления врача")
public class DoctorRequestDto {

    /**
     * ФИО врача
     */
    @Schema(description = "ФИО врача")
    private String name;

    /**
     * Контактный телефон
     */
    @Schema(description = "Контактный телефон")
    private String phone;

    /**
     * Время начала рабочего дня
     */
    @Schema(description = "Время начала рабочего дня")
    private LocalDateTime workHoursFrom;

    /**
     * Время окончания рабочего дня
     */
    @Schema(description = "Время окончания рабочего дня")
    private LocalDateTime workHoursFor;

    /**
     * Ид кабинета
     */
    @Schema(description = "ID кабинета")
    private Long officeId;

    /**
     * Список ID специализаций
     */
    @Schema(description = "Список ID специализаций")
    private Set<Long> specializationIds;

    /**
     * Метод для преобразования в Entity.
     *
     * @return the doctors
     */
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