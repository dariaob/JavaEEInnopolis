package org.dariaob.services;

import lombok.RequiredArgsConstructor;
import org.dariaob.models.PatientCardsHistory;
import org.dariaob.repositories.PatientCardsHistoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Сервис для работы с историей изменений медицинских карт пациентов.
 */
@Service
@RequiredArgsConstructor
public class PatientCardsHistoryService {

    private final PatientCardsHistoryRepository repository;

    /**
     * Получить все изменения по карте пациента.
     * @param cardId идентификатор карты
     * @return список изменений
     */
    public List<PatientCardsHistory> getByCardId(Long cardId) {
        return repository.findByCardId(cardId);
    }

    /**
     * Получить последнее изменение по карте пациента.
     *
     * @param cardId идентификатор карты
     * @return последняя запись изменения
     */
    public Optional<PatientCardsHistory> getLastChangeByCardId(Long cardId) {
        return repository.findTopByCardIdOrderByChangedAtDesc(cardId);
    }

    /**
     * Получить все изменения, внесённые определённым пользователем.
     * @param username имя пользователя (логин)
     * @return список изменений
     */
    public List<PatientCardsHistory> getByChangedBy(String username) {
        return repository.findByChangedBy(username);
    }

    /**
     * Добавить новую запись об изменении карты пациента.
     * @param historyEntry объект истории
     * @return сохранённая запись
     */
    public PatientCardsHistory create(PatientCardsHistory historyEntry) {
        return repository.save(historyEntry);
    }
}
