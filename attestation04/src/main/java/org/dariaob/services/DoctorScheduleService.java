package org.dariaob.services;

import lombok.RequiredArgsConstructor;
import org.dariaob.models.DoctorSchedule;
import org.dariaob.repositories.DoctorScheduleRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Сервис для управления расписанием врачей.
 * Реализует CRUD-операции и soft delete.
 */
@Service
@RequiredArgsConstructor
public class DoctorScheduleService {

    private final DoctorScheduleRepository repository;

    /**
     * Получить все активные (неудалённые) записи расписания.
     *
     * @return список расписаний
     */
    @Cacheable(value = "doctorSchedule", key = "'allActive'")
    public List<DoctorSchedule> getAllActive() {
        return repository.findAllActive();
    }

    /**
     * Получить активную запись расписания по ID.
     *
     * @param id идентификатор расписания
     * @return Optional с расписанием
     */
    @Cacheable(value = "doctorSchedule", key = "#id")
    public Optional<DoctorSchedule> getActiveById(Long id) {
        return repository.findActiveById(id);
    }

    /**
     * Получить все записи по врачу.
     *
     * @param doctorId идентификатор врача
     * @return список расписаний
     */
    @Cacheable(value = "doctorSchedule", key = "'doctor:' + #doctorId")
    public List<DoctorSchedule> getByDoctor(Long doctorId) {
        return repository.findByDoctor(doctorId);
    }

    /**
     * Получить расписание врача по дню недели.
     *
     * @param doctorId  идентификатор врача
     * @param dayOfWeek день недели (1-7)
     * @return список расписаний
     */
    @Cacheable(value = "doctorSchedule", key = "'doctor:' + #doctorId + ':day:' + #dayOfWeek")
    public List<DoctorSchedule> getByDoctorAndDay(Long doctorId, Short dayOfWeek) {
        return repository.findByDoctorAndDay(doctorId, dayOfWeek);
    }

    /**
     * Создать новое расписание.
     *
     * @param schedule объект расписания
     * @return сохранённый объект
     */
    @CacheEvict(value = "doctorSchedule", allEntries = true)
    public DoctorSchedule create(DoctorSchedule schedule) {
        schedule.setDeleted(false);  // Или аналогичное назначение значения по умолчанию
        return repository.save(schedule);
    }

    /**
     * Обновить расписание по ID.
     *
     * @param id      идентификатор существующего расписания
     * @param updated обновлённые данные
     * @return обновлённое расписание
     */
    @CacheEvict(value = "doctorSchedule", allEntries = true)
    public DoctorSchedule update(Long id, DoctorSchedule updated) {
        return repository.findById(id)
                .map(existing -> {
                    existing.setDayOfWeek(updated.getDayOfWeek());
                    existing.setStartTime(updated.getStartTime());
                    existing.setEndTime(updated.getEndTime());
                    existing.setOffice(updated.getOffice());
                    existing.setDayOfWeek(updated.getDayOfWeek());
                    return repository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Расписание не найдено"));
    }

    /**
     * Пометить расписание как удалённое (soft delete).
     *
     * @param id идентификатор расписания
     */
    @CacheEvict(value = "doctorSchedule", allEntries = true)
    public void delete(Long id) {
        repository.softDelete(id); // Используем метод softDelete из репозитория
    }

    /**
     * Восстановить soft-deleted расписание.
     *
     * @param id идентификатор расписания
     */
    @CacheEvict(value = "doctorSchedule", allEntries = true)
    public void restore(Long id) {
        repository.restore(id); // Используем метод restore из репозитория
    }
}
