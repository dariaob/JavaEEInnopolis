package org.dariaob.dto.doctors;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.checkerframework.checker.units.qual.A;
import org.dariaob.dto.offices.OfficeDto;
import org.dariaob.models.DoctorSpecializations;
import org.dariaob.models.Doctors;
import org.dariaob.models.Specializations;
import org.dariaob.utils.DateUtils;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * DTO, содержащий полную информацию о враче.
 * Используется для отображения в ответах API.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Полная информация о враче")
public class DoctorResponseDto {

    /**
     * Уникальный идентификатор врача.
     */
    @Schema(description = "ID врача")
    private Long id;

    /**
     * Полное имя врача.
     */
    @Schema(description = "ФИО врача")
    private String name;

    /**
     * Контактный номер телефона врача.
     */
    @Schema(description = "Контактный телефон")
    private String phone;

    /**
     * Время начала рабочего дня (в формате HH:mm dd.MM.yyyy).
     */
    @Schema(description = "Время начала рабочего дня (формат: HH:mm dd.MM.yyyy)", nullable = true)
    private String workHoursFrom;

    /**
     * Время окончания рабочего дня (в формате HH:mm dd.MM.yyyy).
     */
    @Schema(description = "Время окончания рабочего дня (формат: HH:mm dd.MM.yyyy)", nullable = true)
    private String workHoursFor;

    /**
     * DTO кабинета, закрепленного за врачом.
     */
    @Schema(description = "Кабинет врача", nullable = true)
    private OfficeDto office;

    /**
     * Список названий специализаций врача.
     */
    @Schema(description = "Список специализаций (названия)")
    private Set<String> specializations;

    /**
     * Конструктор, инициализирующий DTO на основе сущности {@link Doctors}.
     *
     * @param doctor объект врача из базы данных
     */
    public DoctorResponseDto(Doctors doctor) {
        this.id = doctor.getId();
        this.name = doctor.getName();
        this.phone = doctor.getPhone();

        this.id = doctor.getId();
        this.name = doctor.getName();
        this.phone = doctor.getPhone();

        // Используем утилиту для форматирования
        this.workHoursFrom = DateUtils.formatDateTime(doctor.getWorkHoursFrom());
        this.workHoursFor = DateUtils.formatDateTime(doctor.getWorkHoursFor());

        // Кабинет
        this.office = Optional.ofNullable(doctor.getOffice())
                .map(OfficeDto::new)
                .orElse(null);

        Set<DoctorSpecializations> safeCopy;
        try {
            synchronized(doctor.getDoctorSpecializations()) {
                safeCopy = new HashSet<>(doctor.getDoctorSpecializations());
            }

            this.specializations = safeCopy.stream()
                    .map(DoctorSpecializations::getSpecialization)
                    .filter(Objects::nonNull)
                    .filter(spec -> !spec.isDeleted())
                    .map(Specializations::getName)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);;
            this.specializations = Set.of(); // Возвращаем пустой список
        }
    }
}
