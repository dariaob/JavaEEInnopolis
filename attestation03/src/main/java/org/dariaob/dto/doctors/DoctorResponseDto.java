package org.dariaob.dto.doctors;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.dariaob.dto.offices.OfficeDto;
import org.dariaob.models.Doctors;
import org.dariaob.models.DoctorSpecializations;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
@Setter
@Schema(description = "Полная информация о враче")
public class DoctorResponseDto {

    @Schema(description = "ID врача")
    private Long id;

    @Schema(description = "ФИО врача")
    private String name;

    @Schema(description = "Контактный телефон")
    private String phone;

    @Schema(description = "Время начала рабочего дня (формат: HH:mm dd.MM.yyyy)", nullable = true)
    private String workHoursFrom;

    @Schema(description = "Время окончания рабочего дня (формат: HH:mm dd.MM.yyyy)", nullable = true)
    private String workHoursFor;

    @Schema(description = "Кабинет врача", nullable = true)
    private OfficeDto office;

    @Schema(description = "Список специализаций (названия)")
    private List<String> specializations;

    // Конструктор для преобразования Entity → DTO
    public DoctorResponseDto(Doctors doctor) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");

        this.id = doctor.getId();
        this.name = doctor.getName();
        this.phone = doctor.getPhone();

        // Безопасная обработка временных полей
        this.workHoursFrom = Optional.ofNullable(doctor.getWorkHoursFrom())
                .map(formatter::format)
                .orElse(null);

        this.workHoursFor = Optional.ofNullable(doctor.getWorkHoursFor())
                .map(formatter::format)
                .orElse(null);

        // Безопасная обработка кабинета
        this.office = Optional.ofNullable(doctor.getOffice())
                .map(OfficeDto::new)
                .orElse(null);

        // Безопасная обработка специализаций
        this.specializations = Optional.ofNullable(doctor.getDoctorSpecializations())
                .map(list -> list.stream()
                        .map(DoctorSpecializations::getSpecialization)
                        .filter(spec -> spec != null && !spec.isDeleted())
                        .map(spec -> spec.getName())
                        .filter(name -> name != null)
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }

    public DoctorResponseDto() {
        this.specializations = Collections.emptyList();
    }
}