package org.dariaob.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.dariaob.exceptions.DataNotFoundException;
import org.dariaob.models.Specializations;
import org.dariaob.repositories.SpecializationsRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Сервис для работы со специализациями.
 */
@Service
@RequiredArgsConstructor
public class SpecializationsService {

    private final SpecializationsRepository specializationsRepository;

    /**
     * Получить все активные специализации (из кэша).
     *
     * @return the all active
     */
    @Cacheable("specializations")
    public List<Specializations> getAllActive() {
        return specializationsRepository.findAllActive();
    }

    /**
     * Найти активную специализацию по ID.
     *
     * @param id the id
     * @return the active by id
     */
    @Cacheable(value = "specializations", key = "#id")
    public Specializations getActiveById(Long id) {
        return specializationsRepository.findActiveById(id)
                .orElseThrow(() -> new DataNotFoundException("Специализация с ID " + id + " не найдена или удалена."));
    }

    /**
     * Найти специализацию по названию (не кэшируем, так как id неизвестен).
     *
     * @param name the name
     * @return the by name ignore case
     */
    public Specializations getByNameIgnoreCase(String name) {
        return specializationsRepository.findFirstByNameIgnoreCaseAndIsDeletedFalseOrderByIdDesc(name)
                .orElseThrow(() -> new DataNotFoundException("Специализация с названием " + name + " не найдена или удалена."));
    }

    /**
     * Мягко удалить специализацию.
     *
     * @param id the id
     */
    @Transactional
    @CacheEvict(value = "specializations", allEntries = true)
    public void softDelete(Long id) {
        if (specializationsRepository.findActiveById(id).isEmpty()) {
            throw new DataNotFoundException("Специализация с ID " + id + " не найдена или уже удалена.");
        }
        specializationsRepository.softDelete(id);
    }

    /**
     * Восстановить удалённую специализацию.
     *
     * @param id the id
     */
    @Transactional
    @CacheEvict(value = "specializations", allEntries = true)
    public void restore(Long id) {
        if (specializationsRepository.findActiveById(id).isEmpty()) {
            throw new DataNotFoundException("Специализация с ID " + id + " не найдена или уже восстановлена.");
        }
        specializationsRepository.restore(id);
    }

    /**
     * Поиск по части названия — не кэшируем, так как ключ неявный и может быть любым.
     *
     * @param namePart the name part
     * @return the list
     */
    public List<Specializations> searchByName(String namePart) {
        return specializationsRepository.searchByName(namePart);
    }

    /**
     * Сохранить или обновить специализацию.
     *
     * @param specialization the specialization
     * @return the specializations
     */
    @Transactional
    @CacheEvict(value = "specializations", allEntries = true)
    public Specializations save(Specializations specialization) {
        return specializationsRepository.save(specialization);
    }
}
