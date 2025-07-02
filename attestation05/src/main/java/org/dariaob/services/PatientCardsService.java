package org.dariaob.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.dariaob.exceptions.DataNotFoundException;
import org.dariaob.models.PatientCards;
import org.dariaob.models.PatientCardsHistory;
import org.dariaob.repositories.PatientCardRepository;
import org.dariaob.repositories.PatientCardsHistoryRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Сервис для работы с картами пациентов.
 */
@Service
@RequiredArgsConstructor
public class PatientCardsService {

    private final PatientCardRepository patientCardRepository;
    private final PatientCardsHistoryRepository patientCardsHistoryRepository;

    /**
     * Получить все активные карты пациентов.
     *
     * @return список активных карт пациентов
     */
    @Cacheable("patientCardsAllActive")
    public List<PatientCards> getAllActive() {
        return patientCardRepository.findAllActive();
    }

    /**
     * Найти активную карту пациента по ID.
     *
     * @param id ID карты пациента
     * @return найденная карта пациента
     * @throws DataNotFoundException если карта не найдена или помечена как удалённая
     */
    @Cacheable(value = "patientCardsById", key = "#id")
    public PatientCards getActiveById(Long id) {
        return patientCardRepository.findActiveById(id)
                .orElseThrow(() -> new DataNotFoundException("Карта пациента с ID " + id + " не найдена или удалена."));
    }

    /**
     * Найти карту пациента по ID пациента.
     *
     * @param patientId ID пациента
     * @return карта пациента
     * @throws DataNotFoundException если карта пациента не найдена или помечена как удалённая
     */
    @Cacheable(value = "patientCardsByPatientId", key = "#patientId")
    public PatientCards getByPatientId(Long patientId) {
        return patientCardRepository.findByPatientId(patientId)
                .orElseThrow(() -> new DataNotFoundException("Карта пациента для пациента с ID " + patientId + " не найдена или удалена."));
    }

    /**
     * Мягко удалить карту пациента (установить флаг isDeleted = true).
     *
     * @param id ID карты пациента
     * @throws DataNotFoundException если карта не найдена или уже удалена
     */
    @Transactional
    @CacheEvict(value = {"patientCardsById", "patientCardsByPatientId", "patientCardsAllActive"}, allEntries = true)
    public void softDelete(Long id) {
        if (patientCardRepository.findActiveById(id).isEmpty()) {
            throw new DataNotFoundException("Карта пациента с ID " + id + " не найдена или уже удалена.");
        }
        patientCardRepository.softDelete(id);
    }

    /**
     * Восстановить карту пациента (установить флаг isDeleted = false).
     *
     * @param id ID карты пациента
     * @throws DataNotFoundException если карта не найдена или уже восстановлена
     */
    @Transactional
    @CacheEvict(value = {"patientCardsById", "patientCardsByPatientId", "patientCardsAllActive"}, allEntries = true)
    public void restore(Long id) {
        // Проверяем существование карты
        PatientCards card = patientCardRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Карта пациента с ID " + id + " не найдена"));

        // Проверяем, что карта удалена (если нужно)
        if (!card.isDeleted()) {
            throw new DataNotFoundException("Карта пациента с ID " + id + " уже восстановлена");
        }

        patientCardRepository.restore(id);
    }

    /**
     * Найти карты по диагнозу (частичное совпадение, без учёта регистра).
     *
     * @param diagnosis часть диагноза
     * @return список карт пациентов
     */
    @Cacheable(value = "patientCardsByDiagnosis", key = "#diagnosis")
    public List<PatientCards> findByDiagnosis(String diagnosis) {
        return patientCardRepository.findByDiagnosisContainingIgnoreCase(diagnosis);
    }

    /**
     * Получить историю изменений карты пациента по ID карты.
     *
     * @param cardId ID карты пациента
     * @return список изменений карты
     */
    @Cacheable(value = "patientCardsHistoryByCardId", key = "#cardId")
    public List<PatientCardsHistory> getHistoryByCardId(Long cardId) {
        return patientCardsHistoryRepository.findByCardId(cardId);
    }

    /**
     * Найти последнее изменение карты пациента по ID карты.
     *
     * @param cardId ID карты пациента
     * @return последнее изменение карты
     * @throws DataNotFoundException если изменений не найдено
     */
    @Cacheable(value = "patientCardsLastChangeByCardId", key = "#cardId")
    public PatientCardsHistory getLastChangeByCardId(Long cardId) {
        return patientCardsHistoryRepository.findTopByCardIdOrderByChangedAtDesc(cardId)
                .orElseThrow(() -> new DataNotFoundException("История изменений для карты с ID " + cardId + " не найдена."));
    }

    /**
     * Сохранить изменения карты пациента.
     *
     * @param card карта пациента
     * @return сохраненная карта пациента
     */
    @Transactional
    @CacheEvict(value = {"patientCardsById", "patientCardsByPatientId", "patientCardsAllActive", "patientCardsByDiagnosis",
            "patientCardsHistoryByCardId", "patientCardsLastChangeByCardId"}, allEntries = true)
    public PatientCards save(PatientCards card) {
        return patientCardRepository.save(card);
    }
}
