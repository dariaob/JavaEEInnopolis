package org.dariaob.repository_tests;

import jakarta.transaction.Transactional;
import org.dariaob.TestWithContainer;
import org.dariaob.models.*;
import org.dariaob.repositories.AppointmentsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * The type Appointments repository test.
 */
@DataJpaTest
@ExtendWith(SpringExtension.class)
@TestPropertySource("classpath:application-test.properties")
public class AppointmentsRepositoryTest extends TestWithContainer {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AppointmentsRepository repository;

    private Long appointmentId1;
    private Long appointmentId2;
    private Long doctorId;
    private Long patientId;

    private Doctors doctor;
    private Patients patient;
    private PatientCards patientCard;
    private Offices office;

    /**
     * Sets up.
     */
    @BeforeEach
    public void setUp() {
        // Создаем офис
        office = new Offices();
        office.setName("Кабинет 100");
        office.setDeleted(false);
        entityManager.persist(office);

        // Создаем врача
        doctor = new Doctors();
        doctor.setName("Доктор Стрэндж");
        doctor.setPhone("+71112223344");
        doctor.setOffice(office);
        doctor.setDeleted(false);
        doctor.setWorkHoursFrom(LocalDateTime.now().withHour(9).withMinute(0));
        doctor.setWorkHoursFor(LocalDateTime.now().withHour(18).withMinute(0));
        entityManager.persist(doctor);
        doctorId = doctor.getId();

        // Создаем карту пациента
        patientCard = new PatientCards();
        patientCard.setSymptoms("Кашель");
        patientCard.setDiagnosis("ОРВИ");
        patientCard.setMeds("Парацетамол");
        patientCard.setDeleted(false);
        entityManager.persist(patientCard);

        // Создаем пациента
        patient = new Patients();
        patient.setName("Иван Иванов");
        patient.setBirthDate(LocalDate.of(1980, 1, 1));
        patient.setPhone("+79990001122");
        patient.setPatientCard(patientCard);
        patient.setDeleted(false);
        patient.setInsuranceId(12345L);
        entityManager.persist(patient);
        patientId = patient.getId();

        // Создаем запись на приём 1
        Appointments appointment1 = new Appointments();
        appointment1.setDate(LocalDateTime.now().plusDays(1));
        appointment1.setDoctor(doctor);
        appointment1.setPatient(patient);
        appointment1.setWorkHoursFrom(LocalDateTime.now().plusDays(1).withHour(10).withMinute(0));
        appointment1.setWorkHoursFor(LocalDateTime.now().plusDays(1).withHour(11).withMinute(0));
        appointment1.setDeleted(false);
        appointment1.setCard(patientCard);
        appointment1.setInsuranceId(patient.getInsuranceId());
        appointment1.setOffice(office);
        entityManager.persist(appointment1);
        appointmentId1 = appointment1.getId();

        // Создаем запись на приём 2 (удалённая)
        Appointments appointment2 = new Appointments();
        appointment2.setDate(LocalDateTime.now().plusDays(2));
        appointment2.setDoctor(doctor);
        appointment2.setPatient(patient);
        appointment2.setWorkHoursFrom(LocalDateTime.now().plusDays(2).withHour(14).withMinute(0));
        appointment2.setWorkHoursFor(LocalDateTime.now().plusDays(2).withHour(15).withMinute(0));
        appointment2.setDeleted(true);
        appointment2.setCard(patientCard);
        appointment2.setInsuranceId(patient.getInsuranceId());
        appointment2.setOffice(office);
        entityManager.persist(appointment2);
        appointmentId2 = appointment2.getId();

        entityManager.flush();
    }

    /**
     * Find all active test.
     */
    @Test
    @DisplayName("Appointments - Repository - Find all active")
    public void findAllActiveTest() {
        List<Appointments> appointments = repository.findAllActive();
        assertThat(appointments, not(empty()));
        assertThat(appointments.stream().allMatch(a -> !a.isDeleted()), is(true));
    }

    /**
     * Find active by id test.
     */
    @Test
    @DisplayName("Appointments - Repository - Find active by ID")
    public void findActiveByIdTest() {
        Optional<Appointments> opt = repository.findActiveById(appointmentId1);
        assertThat(opt.isPresent(), is(true));
        assertThat(opt.get().getId(), equalTo(appointmentId1));
        assertThat(opt.get().isDeleted(), is(false));

        Optional<Appointments> deletedOpt = repository.findActiveById(appointmentId2);
        assertThat(deletedOpt.isPresent(), is(false));
    }

    /**
     * Soft delete test.
     */
    @Test
    @DisplayName("Appointments - Repository - Soft delete")
    @Transactional
    public void softDeleteTest() {
        repository.softDelete(appointmentId1);
        entityManager.flush();

        Optional<Appointments> opt = repository.findActiveById(appointmentId1);
        assertThat(opt.isPresent(), is(false));
    }

    /**
     * Restore test.
     */
    @Test
    @DisplayName("Appointments - Repository - Restore")
    @Transactional
    public void restoreTest() {
        repository.softDelete(appointmentId1);
        entityManager.flush();

        repository.restore(appointmentId1);
        entityManager.flush();

        Optional<Appointments> opt = repository.findActiveById(appointmentId1);
        assertThat(opt.isPresent(), is(true));
        assertThat(opt.get().isDeleted(), is(false));
    }

    /**
     * Find all active by doctor id test.
     */
    @Test
    @DisplayName("Appointments - Repository - Find all active by doctor ID")
    public void findAllActiveByDoctorIdTest() {
        List<Appointments> list = repository.findAllActiveByDoctorId(doctorId);
        assertThat(list, not(empty()));
        assertThat(list.stream().allMatch(a -> a.getDoctor().getId().equals(doctorId) && !a.isDeleted()), is(true));
    }

    /**
     * Find all active by patient id test.
     */
    @Test
    @DisplayName("Appointments - Repository - Find all active by patient ID")
    public void findAllActiveByPatientIdTest() {
        List<Appointments> list = repository.findAllActiveByPatientId(patientId);
        assertThat(list, not(empty()));
        assertThat(list.stream().allMatch(a -> a.getPatient().getId().equals(patientId) && !a.isDeleted()), is(true));
    }

    /**
     * Exists overlapping appointment test.
     */
    @Test
    @DisplayName("Appointments - Repository - Check overlapping appointment")
    public void existsOverlappingAppointmentTest() {
        LocalDateTime from = LocalDateTime.now().plusDays(1).withHour(9);
        LocalDateTime to = LocalDateTime.now().plusDays(1).withHour(10).plusMinutes(30);

        boolean exists = repository.existsOverlappingAppointment(doctorId, from, to);
        assertThat(exists, is(true));

        LocalDateTime noOverlapFrom = LocalDateTime.now().plusDays(1).withHour(11);
        LocalDateTime noOverlapTo = LocalDateTime.now().plusDays(1).withHour(12);

        boolean notExists = repository.existsOverlappingAppointment(doctorId, noOverlapFrom, noOverlapTo);
        assertThat(notExists, is(false));
    }
}
