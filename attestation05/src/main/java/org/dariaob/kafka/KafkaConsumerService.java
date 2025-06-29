package org.dariaob.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dariaob.models.Appointments;
import org.dariaob.models.DoctorScheduleSlot;
import org.dariaob.repositories.AppointmentsRepository;
import org.dariaob.repositories.DoctorScheduleSlotRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerService {

    private final DoctorScheduleSlotRepository slotRepository;
    private final AppointmentsRepository appointmentsRepository;
    private final ObjectMapper objectMapper;

    /**
     * Обрабатывает события о приёмах из топика "appointments".
     * Поддерживаемые события:
     * - APPOINTMENT_CREATED
     * - APPOINTMENT_DELETED
     * - APPOINTMENT_RESTORED
     */
    @KafkaListener(topics = "appointments", groupId = "appointments-consumer-group")
    @Transactional
    public void handleAppointmentEvent(String message) {
        try {
            KafkaMessageDto event = objectMapper.readValue(message, KafkaMessageDto.class);
            log.debug("Processing event: {}", event.getEventType());

            switch (event.getEventType()) {
                case "APPOINTMENT_DELETED" -> handleAppointmentDeleted(event);
                case "APPOINTMENT_RESTORED" -> handleAppointmentRestored(event);
                case "APPOINTMENT_CREATED" -> log.info("Appointment created: {}", event.getEntityId());
                default -> log.warn("Unknown event type: {}", event.getEventType());
            }
        } catch (Exception e) {
            log.error("Error processing Kafka message: {}", message, e);
        }
    }

    /**
     * Обработка отмены приёма:
     * 1. Находим связанный слот
     * 2. Помечаем слот как свободный
     */
    private void handleAppointmentDeleted(KafkaMessageDto event) {
        Optional<Appointments> appointmentOpt = appointmentsRepository.findById(event.getEntityId());
        if (appointmentOpt.isEmpty()) {
            log.warn("Appointment {} not found for DELETE event", event.getEntityId());
            return;
        }

        Appointments appointment = appointmentOpt.get();
        if (appointment.getSlot() != null) {
            DoctorScheduleSlot slot = appointment.getSlot();
            slot.setBooked(false);
            slotRepository.save(slot);
            log.info("Slot {} released for appointment {}", slot.getId(), appointment.getId());
        }
    }

    /**
     * Обработка восстановления приёма:
     * 1. Находим связанный слот
     * 2. Помечаем слот как занятый
     */
    private void handleAppointmentRestored(KafkaMessageDto event) {
        Optional<Appointments> appointmentOpt = appointmentsRepository.findById(event.getEntityId());
        if (appointmentOpt.isEmpty()) {
            log.warn("Appointment {} not found for RESTORE event", event.getEntityId());
            return;
        }

        Appointments appointment = appointmentOpt.get();
        if (appointment.getSlot() != null) {
            DoctorScheduleSlot slot = appointment.getSlot();
            slot.setBooked(true);
            slotRepository.save(slot);
            log.info("Slot {} booked again for appointment {}", slot.getId(), appointment.getId());
        }
    }
}