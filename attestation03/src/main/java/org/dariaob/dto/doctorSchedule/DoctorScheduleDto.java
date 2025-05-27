package org.dariaob.dto.doctorSchedule;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.dariaob.models.DoctorSchedule;
import org.dariaob.models.Doctors;
import org.dariaob.models.Offices;

import java.time.LocalTime;

/**
 * DTO для передачи информации о расписании врача.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO: Расписание врача")
public class DoctorScheduleDto {

    /**
     * Идентификатор записи расписания.
     */
    @Schema(description = "Идентификатор расписания")
    private Long id;

    /**
     * Идентификатор врача.
     */
    @Schema(description = "Идентификатор врача")
    private Long doctorId;

    /**
     * День недели (1 = Понедельник, ..., 7 = Воскресенье).
     */
    @Schema(description = "День недели (1 - понедельник, 7 - воскресенье)")
    private Short dayOfWeek;

    /**
     * Время начала приема.
     */
    @Schema(description = "Время начала приёма")
    private LocalTime startTime;

    /**
     * Время окончания приема.
     */
    @Schema(description = "Время окончания приёма")
    private LocalTime endTime;

    /**
     * Идентификатор кабинета (опционально).
     */
    @Schema(description = "Идентификатор кабинета", nullable = true)
    private Long officeId;

    /**
     * Преобразует DTO в Entity объект DoctorSchedule
     * @return новый экземпляр DoctorSchedule
     */
    public DoctorSchedule toEntity() {
        DoctorSchedule entity = new DoctorSchedule();
        entity.setId(this.id);

        Doctors doctor = new Doctors();
        doctor.setId(this.doctorId);
        entity.setDoctor(doctor);

        entity.setDayOfWeek(this.dayOfWeek);
        entity.setStartTime(this.startTime);
        entity.setEndTime(this.endTime);

        if (this.officeId != null) {
            Offices office = new Offices();
            office.setId(this.officeId);
            entity.setOffice(office);
        }

        // Флаг isDeleted не устанавливаем, так как он по умолчанию false в Entity
        return entity;
    }

    /**
     * Создает DTO из Entity объекта DoctorSchedule
     * @param entity исходный объект DoctorSchedule
     * @return новый экземпляр DoctorScheduleDto
     */
    public static DoctorScheduleDto fromEntity(DoctorSchedule entity) {
        return new DoctorScheduleDto(
                entity.getId(),
                entity.getDoctor() != null ? entity.getDoctor().getId() : null,
                entity.getDayOfWeek(),
                entity.getStartTime(),
                entity.getEndTime(),
                entity.getOffice() != null ? entity.getOffice().getId() : null
        );
    }
}