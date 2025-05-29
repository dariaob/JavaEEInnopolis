package org.dariaob.dto.appointments;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.dariaob.models.Appointments;

import java.time.LocalDateTime;

/**
 * DTO-класс для создания или обновления записи на прием к врачу.
 * Используется при получении данных от клиента через API.
 */
@Getter
@Setter
@Schema(description = "Данные для создания/обновления записи на прием")
public class AppointmentRequestDto {

    /**
     * ID врача, у которого назначен прием.
     */
    @Schema(description = "ID врача")
    private Long doctorId;

    /**
     * ID пациента, который записывается на прием.
     */
    @Schema(description = "ID пациента")
    private Long patientId;

    /**
     * Дата и время проведения приема.
     */
    @Schema(description = "Дата и время приема")
    private LocalDateTime date;

    /**
     * ID кабинета, где будет проходить прием.
     */
    @Schema(description = "ID кабинета")
    private Long officeId;

    /**
     * ID страхового полиса пациента.
     */
    @Schema(description = "ID страхового полиса")
    private Long insuranceId;

    /**
     * Преобразует DTO в сущность устанавливая только дату приема.
     */
    public Appointments toEntity() {
        Appointments appointment = new Appointments();
        appointment.setDate(this.date);
        return appointment;
    }
}
