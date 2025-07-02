package org.dariaob.service_tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.dariaob.dto.appointments.AppointmentRequestDto;
import org.dariaob.exceptions.NoFreeSlotsException;
import org.dariaob.kafka.KafkaProducerService;
import org.dariaob.models.*;
import org.dariaob.repositories.AppointmentsRepository;
import org.dariaob.repositories.DoctorScheduleSlotRepository;
import org.dariaob.services.*;
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

    @Mock
    private DoctorsService doctorsService;

    @Mock
    private PatientsService patientsService;

    @Mock
    private OfficesService officesService;

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
    private AppointmentRequestDto testDto;


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

        testDto = new AppointmentRequestDto();
        testDto.setDoctorId(testDoctor.getId());
        testDto.setPatientId(testPatient.getId());
        testDto.setOfficeId(testOffice.getId());
        testDto.setDate(LocalDateTime.of(2025, 7, 1, 10, 0));
        testDto.setInsuranceId(9999L);

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
        when(doctorsService.getActiveById(testDoctor.getId())).thenReturn(testDoctor);
        when(patientsService.getActiveById(testPatient.getId())).thenReturn(testPatient);
        when(officesService.getActiveOfficeById(testOffice.getId())).thenReturn(testOffice);
        when(repository.existsOverlappingAppointment(eq(testDoctor.getId()), any(), any())).thenReturn(false);
        when(doctorScheduleService.getByDoctorAndDay(eq(testDoctor.getId()), anyShort())).thenReturn(List.of(testSchedule));
        when(slotRepository.existsByDoctorIdAndDate(eq(testDoctor.getId()), any())).thenReturn(false);
        when(slotRepository.findFirstByDoctorIdAndTime(eq(testDoctor.getId()), any(), any()))
                .thenReturn(Optional.of(testSlot));
        when(repository.save(any())).thenReturn(testAppointment);
        doNothing().when(slotGenerator).generateSlotsForDate(eq(testDoctor.getId()), any());

        Appointments created = service.createAppointment(testDto);

        assertThat(created).isNotNull();
        assertThat(created.getDoctor().getId()).isEqualTo(testDoctor.getId());
        assertThat(created.getPatient().getId()).isEqualTo(testPatient.getId());
        verify(slotGenerator).generateSlotsForDate(eq(testDoctor.getId()), any());
    }


    @Test
    @DisplayName("Appointments - Service - Создание приёма - нет свободных слотов")
    void appointmentsCreateNoFreeSlotsTest() {
        when(doctorsService.getActiveById(testDoctor.getId())).thenReturn(testDoctor);
        when(patientsService.getActiveById(testPatient.getId())).thenReturn(testPatient);
        when(officesService.getActiveOfficeById(testOffice.getId())).thenReturn(testOffice);
        when(repository.existsOverlappingAppointment(eq(testDoctor.getId()), any(), any())).thenReturn(false);
        when(doctorScheduleService.getByDoctorAndDay(eq(testDoctor.getId()), anyShort())).thenReturn(List.of(testSchedule));
        when(slotRepository.existsByDoctorIdAndDate(eq(testDoctor.getId()), any())).thenReturn(true);
        when(slotRepository.findFirstByDoctorIdAndTime(eq(testDoctor.getId()), any(), any()))
                .thenReturn(Optional.empty());

        assertThrows(NoFreeSlotsException.class, () -> service.createAppointment(testDto));
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
