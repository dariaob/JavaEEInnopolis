package org.dariaob.service_tests;

import org.dariaob.exceptions.DataNotFoundException;
import org.dariaob.models.*;
import org.dariaob.repositories.AppointmentsRepository;
import org.dariaob.services.AppointmentsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AppointmentsServiceTest {

    @Mock
    private AppointmentsRepository repository;

    @InjectMocks
    private AppointmentsService service;

    private Doctors testDoctor;
    private Patients testPatient;
    private PatientCards testCard;
    private Offices testOffice;
    private Appointments testAppointment;

    @BeforeEach
    public void setup() {
        testDoctor = createTestDoctor(1L, "Dr. Smith");
        testPatient = createTestPatient(1L, "John Doe");
        testCard = createTestPatientCard(1L, testPatient);
        testOffice = createTestOffice(1L, "A101");

        testAppointment = createTestAppointment(
                1L,
                LocalDateTime.of(2023, 6, 15, 10, 0),
                testDoctor,
                testPatient,
                LocalDateTime.of(2023, 6, 15, 10, 0),
                LocalDateTime.of(2023, 6, 15, 10, 30),
                testCard,
                123456L,
                testOffice,
                false
        );
    }

    @Test
    @DisplayName("Appointments - Service - Get all active test")
    public void appointmentGetAllActiveTest() {
        when(repository.findAllActive()).thenReturn(List.of(testAppointment));

        List<Appointments> result = service.getAllActiveAppointments();

        assertThat(result, hasSize(1));
        assertThat(result.get(0).getId(), equalTo(1L));
        verify(repository, times(1)).findAllActive();
    }

    @Test
    @DisplayName("Appointments - Service - Get by ID test - Found")
    public void appointmentGetByIdFoundTest() {
        when(repository.findActiveById(1L)).thenReturn(Optional.of(testAppointment));

        Appointments result = service.getActiveAppointmentById(1L);

        assertThat(result.getDoctor().getName(), equalTo("Dr. Smith"));
    }

    @Test
    @DisplayName("Appointments - Service - Get by ID test - Not found")
    public void appointmentGetByIdNotFoundTest() {
        when(repository.findActiveById(1L)).thenReturn(Optional.empty());

        DataNotFoundException exception = assertThrows(DataNotFoundException.class,
                () -> service.getActiveAppointmentById(1L));

        assertThat(exception.getMessage(), equalTo("Приём с ID 1 не найден или удалён."));
    }

    @Test
    @DisplayName("Appointments - Service - Soft delete test - Success")
    public void appointmentSoftDeleteSuccessTest() {
        when(repository.findActiveById(1L)).thenReturn(Optional.of(testAppointment));
        doNothing().when(repository).softDelete(1L);

        service.softDeleteAppointment(1L);

        verify(repository, times(1)).softDelete(1L);
    }

    @Test
    @DisplayName("Appointments - Service - Soft delete test - Not found")
    public void appointmentSoftDeleteNotFoundTest() {
        when(repository.findActiveById(1L)).thenReturn(Optional.empty());

        DataNotFoundException exception = assertThrows(DataNotFoundException.class,
                () -> service.softDeleteAppointment(1L));

        assertThat(exception.getMessage(), equalTo("Нельзя удалить: приём с ID 1 не найден или уже удалён."));
    }

    @Test
    @DisplayName("Appointments - Service - Restore test")
    public void appointmentRestoreTest() {
        doNothing().when(repository).restore(1L);

        service.restoreAppointment(1L);

        verify(repository, times(1)).restore(1L);
    }

    @Test
    @DisplayName("Appointments - Service - Get by doctor test")
    public void appointmentGetByDoctorTest() {
        when(repository.findAllActiveByDoctorId(1L)).thenReturn(List.of(testAppointment));

        List<Appointments> result = service.getActiveAppointmentsByDoctor(1L);

        assertThat(result, hasSize(1));
        assertThat(result.get(0).getDoctor().getId(), equalTo(1L));
    }

    @Test
    @DisplayName("Appointments - Service - Get by patient test")
    public void appointmentGetByPatientTest() {
        when(repository.findAllActiveByPatientId(1L)).thenReturn(List.of(testAppointment));

        List<Appointments> result = service.getActiveAppointmentsByPatient(1L);

        assertThat(result, hasSize(1));
        assertThat(result.get(0).getPatient().getId(), equalTo(1L));
    }

    @Test
    @DisplayName("Appointments - Service - Time conflict test - Conflict exists")
    public void appointmentTimeConflictExistsTest() {
        LocalDateTime from = LocalDateTime.of(2023, 6, 15, 10, 15);
        LocalDateTime to = LocalDateTime.of(2023, 6, 15, 10, 45);
        when(repository.existsOverlappingAppointment(1L, from, to)).thenReturn(true);

        boolean result = service.hasTimeConflict(1L, from, to);

        assertThat(result, equalTo(true));
    }

    @Test
    @DisplayName("Appointments - Service - Time conflict test - No conflict")
    public void appointmentTimeConflictNoConflictTest() {
        LocalDateTime from = LocalDateTime.of(2023, 6, 15, 11, 0);
        LocalDateTime to = LocalDateTime.of(2023, 6, 15, 11, 30);
        when(repository.existsOverlappingAppointment(1L, from, to)).thenReturn(false);

        boolean result = service.hasTimeConflict(1L, from, to);

        assertThat(result, equalTo(false));
    }

    private Doctors createTestDoctor(Long id, String name) {
        Doctors doctor = new Doctors();
        doctor.setId(id);
        doctor.setName(name);
        return doctor;
    }

    private Patients createTestPatient(Long id, String name) {
        Patients patient = new Patients();
        patient.setId(id);
        patient.setName(name);
        return patient;
    }

    private PatientCards createTestPatientCard(Long id, Patients patient) {
        PatientCards card = new PatientCards();
        card.setId(id);
        card.setPatient(patient);
        return card;
    }

    private Offices createTestOffice(Long id, String name) {
        Offices office = new Offices();
        office.setId(id);
        office.setName(name);
        return office;
    }

    private Appointments createTestAppointment(Long id, LocalDateTime date, Doctors doctor,
                                               Patients patient, LocalDateTime workHoursFrom,
                                               LocalDateTime workHoursFor, PatientCards card,
                                               Long insuranceId, Offices office, boolean isDeleted) {
        Appointments appointment = new Appointments();
        appointment.setId(id);
        appointment.setDate(date);
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setWorkHoursFrom(workHoursFrom);
        appointment.setWorkHoursFor(workHoursFor);
        appointment.setCard(card);
        appointment.setInsuranceId(insuranceId);
        appointment.setOffice(office);
        appointment.setDeleted(isDeleted);
        return appointment;
    }
}