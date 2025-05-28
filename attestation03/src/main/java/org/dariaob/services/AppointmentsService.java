package org.dariaob.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.dariaob.exceptions.DataNotFoundException;
import org.dariaob.models.Appointments;
import org.dariaob.repositories.AppointmentsRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Сервис для работы с приёмами
 */
@Service
@RequiredArgsConstructor
public class AppointmentsService {

    private final AppointmentsRepository appointmentsRepository;

    /**
     * Получить все активные приёмы
     */
    public List<Appointments> getAllActiveAppointments() {
        return appointmentsRepository.findAllActive();
    }

    /**
     * Получить активный приём по ID или выбросить исключение
     */
    public Appointments getActiveAppointmentById(Long id) {
        return appointmentsRepository.findActiveById(id)
                .orElseThrow(() -> new DataNotFoundException("Приём с ID " + id + " не найден или удалён."));
    }

    /**
     * Мягко удалить приём
     */
    @Transactional
    public void softDeleteAppointment(Long id) {
        if (appointmentsRepository.findActiveById(id).isEmpty()) {
            throw new DataNotFoundException("Нельзя удалить: приём с ID " + id + " не найден или уже удалён.");
        }
        appointmentsRepository.softDelete(id);
    }

    /**
     * Восстановить приём
     */
    @Transactional
    public void restoreAppointment(Long id) {
        appointmentsRepository.restore(id);
    }

    /**
     * Получить все активные приёмы по врачу
     */
    public List<Appointments> getActiveAppointmentsByDoctor(Long doctorId) {
        return appointmentsRepository.findAllActiveByDoctorId(doctorId);
    }

    /**
     * Получить все активные приёмы по пациенту
     */
    public List<Appointments> getActiveAppointmentsByPatient(Long patientId) {
        return appointmentsRepository.findAllActiveByPatientId(patientId);
    }

    /**
     * Проверить, есть ли пересекающийся приём у врача (для проверки конфликта расписания)
     */
    public boolean hasTimeConflict(Long doctorId, LocalDateTime from, LocalDateTime to) {
        return appointmentsRepository.existsOverlappingAppointment(doctorId, from, to);
    }
}
