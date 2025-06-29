package org.dariaob.service_tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.dariaob.exceptions.NoFreeSlotsException;
import org.dariaob.kafka.KafkaProducerService;
import org.dariaob.models.*;
import org.dariaob.repositories.AppointmentsRepository;
import org.dariaob.repositories.DoctorScheduleSlotRepository;
import org.dariaob.services.AppointmentsService;
import org.dariaob.services.DoctorScheduleService;
import org.dariaob.services.DoctorScheduleSlotGeneratorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Appointments - Service - Unit тесты")
class AppointmentsServiceTest {

    @Mock
    private AppointmentsRepository repository;

    @Mock
    private DoctorScheduleService doctorScheduleService;

    @Mock
    private DoctorScheduleSlotRepository slotRepository;

    @InjectMocks
    private AppointmentsService service;
    @Mock
    private DoctorScheduleSlotGeneratorService slotGenerator;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private KafkaProducerService kafkaProducerService;

    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    private Appointments testAppointment;
    private Doctors testDoctor;
    private Patients testPatient;
    private PatientCards testCard;
    private Offices testOffice;
    private DoctorSchedule testSchedule;
    private DoctorScheduleSlot testSlot;

    @BeforeEach
    void setup() throws JsonProcessingException {
        testDoctor = createTestDoctor(1L, "Доктор Хаус");
        testPatient = createTestPatient(1L, "Пациент Иванов");
        testCard = createTestPatientCard(1L, testPatient);
        testOffice = createTestOffice(1L, "Процедурная");

        LocalDateTime from = LocalDateTime.of(2025, 7, 1, 10, 0);
        LocalDateTime to = LocalDateTime.of(2025, 7, 1, 10, 30);
        LocalDateTime date = from;

        testSchedule = createTestSchedule(LocalTime.of(9, 0), LocalTime.of(17, 0));
        testSlot = createTestSlot(1L, testSchedule, false);

        testAppointment = createTestAppointment(
                1L,
                date,
                testDoctor,
                testPatient,
                from,
                to,
                false,
                testCard,
                9999L,
                testOffice,
                testSlot
        );
    }

    @Test
    @DisplayName("Appointments - Service - Создание приёма - успешный сценарий")
    void appointmentsCreateSuccessTest() {
        when(repository.existsOverlappingAppointment(anyLong(), any(), any())).thenReturn(false);
        when(doctorScheduleService.getByDoctorAndDay(anyLong(), anyShort())).thenReturn(List.of(testSchedule));
        when(slotRepository.existsByDoctorIdAndDate(anyLong(), any())).thenReturn(false); // Слотов нет!
        when(slotRepository.findFirstByDoctorIdAndTime(anyLong(), any(), any()))
                .thenReturn(Optional.of(testSlot));
        when(repository.save(any())).thenReturn(testAppointment);

        doNothing().when(slotGenerator).generateSlotsForDate(anyLong(), any());

        Appointments created = service.createAppointment(testAppointment);

        assertThat(created).isNotNull();
        verify(slotGenerator).generateSlotsForDate(anyLong(), any());
}

    @Test
    @DisplayName("Appointments - Service - Создание приёма - нет свободных слотов")
    void appointmentsCreateNoFreeSlotsTest() {
        when(repository.existsOverlappingAppointment(anyLong(), any(), any())).thenReturn(false);
        when(doctorScheduleService.getByDoctorAndDay(anyLong(), anyShort())).thenReturn(List.of(testSchedule));
        when(slotRepository.existsByDoctorIdAndDate(anyLong(), any())).thenReturn(true);
        when(slotRepository.findFirstByDoctorIdAndTime(anyLong(), any(), any()))
                .thenReturn(Optional.empty());

        assertThrows(NoFreeSlotsException.class,
                () -> service.createAppointment(testAppointment));
    }


    @Test
    @DisplayName("Appointments - Кэширование - Повторный запрос")
    void appointmentsCacheTest() {
        List<Appointments> expectedAppointments = List.of(testAppointment);
        when(repository.findAllActive()).thenReturn(expectedAppointments);

        List<Appointments> firstCall = service.getAllActiveAppointments();

        assertThat(firstCall).isEqualTo(expectedAppointments);
        verify(repository, times(1)).findAllActive();

        List<Appointments> secondCall = service.getAllActiveAppointments();

        assertThat(secondCall).isEqualTo(firstCall);
        verifyNoMoreInteractions(repository);
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

    private DoctorSchedule createTestSchedule(LocalTime from, LocalTime to) {
        DoctorSchedule schedule = new DoctorSchedule();
        schedule.setStartTime(from);
        schedule.setEndTime(to);
        return schedule;
    }

    private DoctorScheduleSlot createTestSlot(Long id, DoctorSchedule schedule, boolean booked) {
        DoctorScheduleSlot slot = new DoctorScheduleSlot();
        slot.setId(id);
        slot.setDoctorSchedule(schedule);
        slot.setBooked(booked);
        return slot;
    }

    private Appointments createTestAppointment(Long id, LocalDateTime date, Doctors doctor, Patients patient,
                                               LocalDateTime workFrom, LocalDateTime workTo, boolean isDeleted,
                                               PatientCards card, Long insuranceId, Offices office,
                                               DoctorScheduleSlot slot) {
        Appointments appointment = new Appointments();
        appointment.setId(id);
        appointment.setDate(date);
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setWorkHoursFrom(workFrom);
        appointment.setWorkHoursFor(workTo);
        appointment.setDeleted(isDeleted);
        appointment.setCard(card);
        appointment.setInsuranceId(insuranceId);
        appointment.setOffice(office);
        appointment.setSlot(slot);
        return appointment;
    }
}
