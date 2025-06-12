package org.dariaob.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.dariaob.exceptions.DataNotFoundException;
import org.dariaob.models.Patients;
import org.dariaob.repositories.PatientsRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Сервис для работы с пациентами.
 */
@Service
@RequiredArgsConstructor
public class PatientsService {

    private final PatientsRepository patientsRepository;

    /**
     * Получить всех активных пациентов.
     *
     * @return список активных пациентов
     */
    @Cacheable("patientsAllActive")
    public List<Patients> getAllActive() {
        return patientsRepository.findAllActive();
    }

    /**
     * Найти активного пациента по ID.
     *
     * @param id ID пациента
     * @return найденный пациент
     * @throws DataNotFoundException если пациент не найден или помечен как удалённый
     */
    @Cacheable(value = "patientsById", key = "#id")
    public Patients getActiveById(Long id) {
        return patientsRepository.findActiveById(id)
                .orElseThrow(() -> new DataNotFoundException("Пациент с ID " + id + " не найден или удалён."));
    }

    /**
     * Найти активного пациента по номеру телефона.
     *
     * @param phone телефон пациента
     * @return найденный пациент
     * @throws DataNotFoundException если пациент с таким номером телефона не найден или удалён
     */
    @Cacheable(value = "patientsByPhone", key = "#phone")
    public Patients getActiveByPhone(String phone) {
        return patientsRepository.findByPhone(phone)
                .orElseThrow(() -> new DataNotFoundException("Пациент с телефоном " + phone + " не найден или удалён."));
    }

    /**
     * Мягко удалить пациента (установить флаг isDeleted = true).
     *
     * @param id ID пациента
     * @throws DataNotFoundException если пациент не найден или уже удалён
     */
    @Transactional
    @CacheEvict(value = {"patientsAllActive", "patientsById", "patientsByPhone"}, allEntries = true)
    public void softDelete(Long id) {
        if (patientsRepository.findActiveById(id).isEmpty()) {
            throw new DataNotFoundException("Пациент с ID " + id + " не найден или уже удалён.");
        }
        patientsRepository.softDelete(id);
    }

    /**
     * Восстановить пациента (установить флаг isDeleted = false).
     *
     * @param id ID пациента
     * @throws DataNotFoundException если пациент не найден или уже восстановлен
     */
    @Transactional
    @CacheEvict(value = {"patientsAllActive", "patientsById", "patientsByPhone"}, allEntries = true)
    public void restore(Long id) {
        if (patientsRepository.findActiveById(id).isEmpty()) {
            throw new DataNotFoundException("Пациент с ID " + id + " не найден или уже восстановлен.");
        }
        patientsRepository.restore(id);
    }

    /**
     * Сохранить пациента.
     *
     * @param patient новый пациент
     * @return сохранённый пациент
     */
    @Transactional
    @CacheEvict(value = {"patientsAllActive", "patientsById", "patientsByPhone"}, allEntries = true)
    public Patients save(Patients patient) {
        return patientsRepository.save(patient);
    }
}
