package org.dariaob.dto.appointments;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.dariaob.dto.doctors.DoctorResponseDto;
import org.dariaob.dto.offices.OfficeDto;
import org.dariaob.dto.patientCards.PatientCardResponseDto;
import org.dariaob.dto.patients.PatientDto;
import org.dariaob.models.*;

import static org.dariaob.utils.DateUtils.formatDateTime;

/**
 * DTO для предоставления полной информации о записи на прием.
 * Включает сведения о враче, пациенте, кабинете и медицинской карте.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Полная информация о записи на прием")
public class AppointmentResponseDto {

    /**
     * Уникальный идентификатор записи.
     */
    @Schema(description = "ID записи")
    private Long id;

    /**
     * Дата и время приема в формате "dd.MM.yyyy HH:mm".
     */
    @Schema(description = "Форматированная дата приема (dd.MM.yyyy HH:mm)")
    private String formattedDate;

    /**
     * Информация о враче, у которого назначен прием.
     */
    @Schema(description = "Полная информация о враче")
    private DoctorResponseDto doctor;

    /**
     * Информация о пациенте, записанном на прием.
     */
    @Schema(description = "Полная информация о пациенте")
    private PatientDto patient;

    /**
     * Данные о кабинете, где проходит прием.
     */
    @Schema(description = "Информация о кабинете")
    private OfficeDto office;

    /**
     * Данные медицинской карты, связанные с приемом.
     */
    @Schema(description = "Данные медицинской карты")
    private PatientCardResponseDto card;

    /**
     * Конструктор, преобразующий сущность {@link Appointments} в DTO.
     *
     * @param appointment объект записи на прием
     */
    public AppointmentResponseDto(Appointments appointment) {
        this.id = appointment.getId();
        this.formattedDate = formatDateTime(appointment.getDate());
        this.doctor = convertDoctorToDto(appointment.getDoctor());
        this.patient = convertPatientToDto(appointment.getPatient());
        this.office = convertOfficeToDto(appointment.getOffice());
        this.card = convertCardToDto(appointment.getCard());
    }

    /**
     * Преобразует сущность врача в DTO.
     *
     * @param doctor объект {@link Doctors}
     * @return {@link DoctorResponseDto}
     */
    private DoctorResponseDto convertDoctorToDto(Doctors doctor) {
        if (doctor == null) return null;

        DoctorResponseDto dto = new DoctorResponseDto();
        dto.setId(doctor.getId());
        dto.setName(doctor.getName());
        dto.setPhone(doctor.getPhone());
        return dto;
    }

    /**
     * Преобразует сущность пациента в DTO.
     *
     * @param patient объект {@link Patients}
     * @return {@link PatientDto}
     */
    private PatientDto convertPatientToDto(Patients patient) {
        if (patient == null) return null;

        PatientDto dto = new PatientDto();
        dto.setId(patient.getId());
        dto.setName(patient.getName());
        dto.setBirthDate(patient.getBirthDate());
        dto.setPhone(patient.getPhone());
        dto.setInsuranceId(patient.getInsuranceId());
        dto.setDeleted(patient.isDeleted());
        return dto;
    }

    /**
     * Преобразует сущность кабинета в DTO.
     *
     * @param office объект {@link Offices}
     * @return {@link OfficeDto}
     */
    private OfficeDto convertOfficeToDto(Offices office) {
        if (office == null) return null;

        OfficeDto dto = new OfficeDto();
        dto.setId(office.getId());
        dto.setName(office.getName());
        return dto;
    }

    /**
     * Преобразует сущность медицинской карты в DTO.
     *
     * @param card объект {@link PatientCards}
     * @return {@link PatientCardResponseDto}
     */
    private PatientCardResponseDto convertCardToDto(PatientCards card) {
        if (card == null) return null;

        PatientCardResponseDto dto = new PatientCardResponseDto();
        dto.setId(card.getId());
        dto.setSymptoms(card.getSymptoms());
        dto.setDiagnosis(card.getDiagnosis());
        dto.setMeds(card.getMeds());
        return dto;
    }
}
