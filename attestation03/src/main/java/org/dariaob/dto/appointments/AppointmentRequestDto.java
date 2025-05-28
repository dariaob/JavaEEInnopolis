package org.dariaob.dto.appointments;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.dariaob.models.Appointments;

import java.time.LocalDateTime;

@Getter
@Setter
@Schema(description = "Данные для создания/обновления записи на прием")
public class AppointmentRequestDto {

    @Schema(description = "ID врача")
    private Long doctorId;

    @Schema(description = "ID пациента")
    private Long patientId;

    @Schema(description = "Дата и время приема")
    private LocalDateTime date;

    @Schema(description = "ID кабинета")
    private Long officeId;

    @Schema(description = "ID страхового полиса")
    private Long insuranceId;

    public Appointments toEntity() {
        Appointments appointment = new Appointments();
        appointment.setDate(this.date);
        return appointment;
    }
}