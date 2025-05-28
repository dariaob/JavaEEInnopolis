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
 * Содержит все связанные данные: врача, пациента, кабинет и медицинскую карту.
 * Автоматически форматирует дату для удобного отображения.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Полная информация о записи на прием")
public class AppointmentResponseDto {

    @Schema(description = "ID записи")
    private Long id;

    @Schema(description = "Форматированная дата приема (dd.MM.yyyy HH:mm)")
    private String formattedDate;

    @Schema(description = "Полная информация о враче")
    private DoctorResponseDto doctor;

    @Schema(description = "Полная информация о пациенте")
    private PatientDto patient;

    @Schema(description = "Информация о кабинете")
    private OfficeDto office;

    @Schema(description = "Данные медицинской карты")
    private PatientCardResponseDto card;

    public AppointmentResponseDto(Appointments appointment) {
        this.id = appointment.getId();
        this.formattedDate = formatDateTime(appointment.getDate());
        this.doctor = convertDoctorToDto(appointment.getDoctor());
        this.patient = convertPatientToDto(appointment.getPatient());
        this.office = convertOfficeToDto(appointment.getOffice());
        this.card = convertCardToDto(appointment.getCard());
    }

    private DoctorResponseDto convertDoctorToDto(Doctors doctor) {
        if (doctor == null) return null;

        DoctorResponseDto dto = new DoctorResponseDto();
        dto.setId(doctor.getId());
        dto.setName(doctor.getName());
        dto.setPhone(doctor.getPhone());
        return dto;
    }

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

    private OfficeDto convertOfficeToDto(Offices office) {
        if (office == null) return null;

        OfficeDto dto = new OfficeDto();
        dto.setId(office.getId());
        dto.setName(office.getName());
        return dto;
    }

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