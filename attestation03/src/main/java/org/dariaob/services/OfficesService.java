package org.dariaob.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.dariaob.exceptions.DataNotFoundException;
import org.dariaob.models.Offices;
import org.dariaob.repositories.OfficesRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Сервис для работы с офисами
 */
@Service
@RequiredArgsConstructor
public class OfficesService {

    private final OfficesRepository officesRepository;

    /**
     * Получить все активные офисы (используется кэш).
     */
    @Cacheable("offices")
    public List<Offices> getAllActiveOffices() {
        return officesRepository.findAllActive();
    }

    /**
     * Получить активный офис по ID (используется кэш).
     */
    @Cacheable(value = "offices", key = "#id")
    public Offices getActiveOfficeById(Long id) {
        return officesRepository.findActiveById(id)
                .orElseThrow(() -> new DataNotFoundException("Офис с ID " + id + " не найден или удалён."));
    }

    /**
     * Мягко удалить офис и очистить кэш.
     */
    @Transactional
    @CacheEvict(value = "offices", allEntries = true)
    public void softDeleteOffice(Long id) {
        if (officesRepository.findActiveById(id).isEmpty()) {
            throw new DataNotFoundException("Нельзя удалить: офис с ID " + id + " не найден или уже удалён.");
        }
        officesRepository.softDelete(id);
    }

    /**
     * Восстановить офис и очистить кэш.
     */
    @Transactional
    @CacheEvict(value = "offices", allEntries = true)
    public void restoreOffice(Long id) {
        officesRepository.restore(id);
    }

    /**
     * Сохранить или обновить офис и очистить кэш.
     */
    @Transactional
    @CacheEvict(value = "offices", allEntries = true)
    public Offices saveOffice(Offices office) {
        return officesRepository.save(office);
    }

    /**
     * Удалить все офисы
     */
    @Transactional
    @CacheEvict(value = "offices", allEntries = true)
    public void softDeleteAll() {
        officesRepository.softDeleteAll();
    }
}
