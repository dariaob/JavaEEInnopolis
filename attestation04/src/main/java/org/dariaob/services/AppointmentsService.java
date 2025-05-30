package org.dariaob.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.dariaob.exceptions.DataNotFoundException;
import org.dariaob.models.Appointments;
import org.dariaob.repositories.AppointmentsRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
     *
     * @return the all active appointments
     */
    @Cacheable(value = "appointments", key = "'allActive'")
    public List<Appointments> getAllActiveAppointments() {
        return appointmentsRepository.findAllActive();
    }

    /**
     * Получить активный приём по ID или выбросить исключение
     *
     * @param id the id
     * @return the active appointment by id
     */
    @Cacheable(value = "appointments", key = "#id")
    public Appointments getActiveAppointmentById(Long id) {
        return appointmentsRepository.findActiveById(id)
                .orElseThrow(() -> new DataNotFoundException("Приём с ID " + id + " не найден или удалён."));
    }

    /**
     * Мягко удалить приём
     *
     * @param id the id
     */
    @Transactional
    @CacheEvict(value = "appointments", allEntries = true)
    public void softDeleteAppointment(Long id) {
        if (appointmentsRepository.findActiveById(id).isEmpty()) {
            throw new DataNotFoundException("Нельзя удалить: приём с ID " + id + " не найден или уже удалён.");
        }
        appointmentsRepository.softDelete(id);
    }

    /**
     * Восстановить приём
     *
     * @param id the id
     */
    @Transactional
    @CacheEvict(value = "appointments", allEntries = true)
    public void restoreAppointment(Long id) {
        appointmentsRepository.restore(id);
    }

    /**
     * Получить все активные приёмы по врачу
     *
     * @param doctorId the doctor id
     * @return the active appointments by doctor
     */
    @Cacheable(value = "appointments", key = "'doctor:' + #doctorId")
    public List<Appointments> getActiveAppointmentsByDoctor(Long doctorId) {
        return appointmentsRepository.findAllActiveByDoctorId(doctorId);
    }

    /**
     * Получить все активные приёмы по пациенту
     *
     * @param patientId the patient id
     * @return the active appointments by patient
     */
    @Cacheable(value = "appointments", key = "'patient:' + #patientId")
    public List<Appointments> getActiveAppointmentsByPatient(Long patientId) {
        return appointmentsRepository.findAllActiveByPatientId(patientId);
    }

    /**
     * Проверить, есть ли пересекающийся приём у врача (для проверки конфликта расписания)
     *
     * @param doctorId the doctor id
     * @param from     the from
     * @param to       the to
     * @return the boolean
     */
    public boolean hasTimeConflict(Long doctorId, LocalDateTime from, LocalDateTime to) {
        return appointmentsRepository.existsOverlappingAppointment(doctorId, from, to);
    }
}
