package org.dariaob.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dariaob.dto.appointments.AppointmentRequestDto;
import org.dariaob.exceptions.DataNotFoundException;
import org.dariaob.exceptions.NoFreeSlotsException;
import org.dariaob.kafka.KafkaMessageDto;
import org.dariaob.kafka.KafkaProducerService;
import org.dariaob.models.*;
import org.dariaob.repositories.AppointmentsRepository;
import org.dariaob.repositories.DoctorScheduleSlotRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * Сервис для работы с приёмами (Appointments).
 * Обеспечивает CRUD-операции, кэширование, проверку пересечений и интеграцию с Kafka и расписанием врача.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentsService {

    private final AppointmentsRepository appointmentsRepository;
    private final KafkaProducerService kafkaProducerService;
    private final DoctorScheduleService doctorScheduleService;
    private final ObjectMapper objectMapper;
    private final DoctorScheduleSlotRepository slotRepository;
    private final DoctorScheduleSlotGeneratorService slotGenerator;
    private final DoctorsService doctorsService;
    private final PatientsService patientsService;
    private final OfficesService officesService;

    private static final String TOPIC = "appointments";

    /**
     * Получить список всех активных (неудалённых) приёмов.
     */
    @Cacheable(value = "appointments", key = "'allActive'")
    public List<Appointments> getAllActiveAppointments() {
        return appointmentsRepository.findAllActive();
    }

    /**
     * Получить один активный приём по ID.
     */
    @Cacheable(value = "appointments", key = "#id")
    public Appointments getActiveAppointmentById(Long id) {
        return appointmentsRepository.findActiveById(id)
                .orElseThrow(() -> new DataNotFoundException("Приём с ID " + id + " не найден или удалён."));
    }

    /**
     * Мягко удалить приём (soft delete) и отправить Kafka-сообщение.
     */
    @Transactional
    @CacheEvict(value = "appointments", allEntries = true)
    public void softDeleteAppointment(Long id) {
        Appointments appointment = appointmentsRepository.findActiveById(id)
                .orElseThrow(() -> new DataNotFoundException("Нельзя удалить: приём с ID " + id + " не найден или уже удалён."));

        if (appointment.getSlot() != null) {
            DoctorScheduleSlot slot = appointment.getSlot();
            slot.setBooked(false);
            slotRepository.save(slot);
        }

        appointmentsRepository.softDelete(id);
        sendKafkaEvent("APPOINTMENT_DELETED", id, appointment);
    }

    /**
     * Восстановить ранее удалённый приём (soft restore) и отправить Kafka-сообщение.
     */
    @Transactional
    @CacheEvict(value = "appointments", allEntries = true)
    public void restoreAppointment(Long id) {
        appointmentsRepository.restore(id);
        Appointments restored = appointmentsRepository.findActiveById(id).orElse(null);
        sendKafkaEvent("APPOINTMENT_RESTORED", id, restored);
    }

    /**
     * Получить список всех активных приёмов по врачу.
     */
    @Cacheable(value = "appointments", key = "'doctor:' + #doctorId")
    public List<Appointments> getActiveAppointmentsByDoctor(Long doctorId) {
        return appointmentsRepository.findAllActiveByDoctorId(doctorId);
    }

    /**
     * Получить список всех активных приёмов по пациенту.
     */
    @Cacheable(value = "appointments", key = "'patient:' + #patientId")
    public List<Appointments> getActiveAppointmentsByPatient(Long patientId) {
        return appointmentsRepository.findAllActiveByPatientId(patientId);
    }

    /**
     * Проверка на пересечение по времени у врача.
     *
     * @return true если найдено пересечение, иначе false
     */
    public boolean hasTimeConflict(Long doctorId, LocalDateTime from, LocalDateTime to) {
        return appointmentsRepository.existsOverlappingAppointment(doctorId, from, to);
    }

    /**
     * Проверка, входит ли указанный интервал в расписание врача.
     */
    private boolean isWithinDoctorSchedule(Doctors doctor, LocalDateTime start, LocalDateTime end) {
        short dayOfWeek = (short) start.getDayOfWeek().getValue();
        List<DoctorSchedule> schedules = doctorScheduleService.getByDoctorAndDay(doctor.getId(), dayOfWeek);

        return schedules.stream()
                .filter(schedule -> !schedule.isDeleted())
                .anyMatch(schedule ->
                        !start.toLocalTime().isBefore(schedule.getStartTime()) &&
                                !end.toLocalTime().isAfter(schedule.getEndTime())
                );
    }

    /**
     * Создать новый приём. Проверяет пересечения и расписание врача.
     */
    @Transactional
    @CacheEvict(value = "appointments", allEntries = true)
    public Appointments createAppointment(AppointmentRequestDto dto) {

        // 1. Получение сущностей по ID из запроса
        Doctors doctor = doctorsService.getActiveById(dto.getDoctorId());
        Patients patient = patientsService.getActiveById(dto.getPatientId());
        Offices office = officesService.getActiveOfficeById(dto.getOfficeId());

        // 2. Работа со временем
        LocalDateTime start = dto.getDate();
        LocalDateTime end = start.plusMinutes(30); // предположим, приём длится 30 минут

        // 3. Проверки
        if (hasTimeConflict(doctor.getId(), start, end)) {
            throw new IllegalArgumentException("Указанное время пересекается с другим приёмом врача.");
        }

        if (!isWithinDoctorSchedule(doctor, start, end)) {
            throw new IllegalArgumentException("Указанное время не соответствует расписанию врача.");
        }

        // 4. Генерация слотов при необходимости
        LocalDate date = start.toLocalDate();
        if (!slotRepository.existsByDoctorIdAndDate(doctor.getId(), date)) {
            slotGenerator.generateSlotsForDate(doctor.getId(), date);
        }

        // 5. Поиск свободного слота
        DoctorScheduleSlot slot = slotRepository
                .findFirstByDoctorIdAndTime(doctor.getId(), start, end)
                .orElseThrow(() -> new NoFreeSlotsException("Нет свободных слотов в это время"));

        // 6. Формирование сущности приёма
        Appointments appointment = new Appointments();
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setOffice(office);
        appointment.setInsuranceId(dto.getInsuranceId());
        appointment.setDate(start);
        appointment.setWorkHoursFrom(start);
        appointment.setWorkHoursFor(end);
        appointment.setSlot(slot);

        slot.setBooked(true);
        slotRepository.save(slot);

        Appointments saved = appointmentsRepository.save(appointment);
        sendKafkaEvent("APPOINTMENT_CREATED", saved.getId(), saved);

        return saved;
    }


    /**
     * Отправить событие в Kafka, сериализовав payload как JSON.
     */
    private void sendKafkaEvent(String eventType, Long id, Object payload) {
        try {
            String payloadJson = objectMapper.writeValueAsString(payload);
            KafkaMessageDto message = KafkaMessageDto.builder()
                    .eventType(eventType)
                    .entityId(id)
                    .eventTime(LocalDateTime.now())
                    .payload(payloadJson)
                    .build();

            kafkaProducerService.sendMessage(TOPIC, message);
        } catch (JsonProcessingException e) {
            log.error("Ошибка сериализации Kafka payload: {}", payload, e);
        }
    }
}
