package org.dariaob.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.dariaob.exceptions.DataNotFoundException;
import org.dariaob.models.Doctors;
import org.dariaob.repositories.DoctorsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Сервис для работы с врачами.
 */
@Service
@RequiredArgsConstructor
public class DoctorsService {

    private final DoctorsRepository doctorsRepository;

    /**
     * Получить все активные записи о врачах.
     *
     * @return список активных врачей
     */
    public List<Doctors> getAllActive() {
        return doctorsRepository.findAllActive();
    }

    /**
     * Найти активного врача по ID.
     *
     * @param id ID врача
     * @return найденный врач
     * @throws DataNotFoundException если врач не найден или помечен как удалённый
     */
    public Doctors getActiveById(Long id) {
        return doctorsRepository.findActiveById(id)
                .orElseThrow(() -> new DataNotFoundException("Врач с ID " + id + " не найден или удалён."));
    }

    /**
     * Найти активного врача по телефону.
     *
     * @param phone телефон врача
     * @return найденный врач
     * @throws DataNotFoundException если врач с таким телефоном не найден или удалён
     */
    public Doctors getActiveByPhone(String phone) {
        return doctorsRepository.findActiveByPhone(phone)
                .orElseThrow(() -> new DataNotFoundException("Врач с телефоном " + phone + " не найден или удалён."));
    }

    /**
     * Мягко удалить врача (установить флаг isDeleted = true).
     *
     * @param id ID врача
     * @throws DataNotFoundException если врач не найден или уже удалён
     */
    @Transactional
    public void softDelete(Long id) {
        if (doctorsRepository.findActiveById(id).isEmpty()) {
            throw new DataNotFoundException("Врач с ID " + id + " не найден или уже удалён.");
        }
        doctorsRepository.softDelete(id);
    }

    /**
     * Восстановить врача (установить флаг isDeleted = false).
     *
     * @param id ID врача
     * @throws DataNotFoundException если врач не найден или уже восстановлен
     */
    @Transactional
    public void restore(Long id) {
        if (doctorsRepository.findActiveById(id).isEmpty()) {
            throw new DataNotFoundException("Врач с ID " + id + " не найден или уже восстановлен.");
        }
        doctorsRepository.restore(id);
    }

    /**
     * Сохранить или обновить данные врача.
     * @param doctor объект врача для сохранения
     * @return сохранённый объект врача
     */
    @Transactional
    public Doctors save(Doctors doctor) {
        return doctorsRepository.save(doctor);
    }
}

