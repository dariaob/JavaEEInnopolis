package org.dariaob.repositories;

import org.dariaob.models.PatientCardsHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с историей карточек пациентов.
 */
@Repository
public interface PatientCardsHistoryRepository extends JpaRepository<PatientCardsHistory, Long> {

    /**
     * Найти все изменения по ID карты пациента
     * @param cardId ID медицинской карты
     * @return список записей истории изменений
     */
    @Query("SELECT h FROM PatientCardsHistory h WHERE h.card.id = ?1 ORDER BY h.changedAt DESC")
    List<PatientCardsHistory> findByCardId(Long cardId);

    /**
     * Найти последнее изменение по ID карты пациента
     * @param cardId ID медицинской карты
     * @return последняя запись истории или Optional.empty()
     */
    Optional<PatientCardsHistory> findTopByCardIdOrderByChangedAtDesc(@Param("cardId") Long cardId);

    /**
     * Найти изменения по имени пользователя, вносившего изменения
     * @param changedBy имя пользователя (логин)
     * @return список записей истории изменений
     */
    @Query("SELECT h FROM PatientCardsHistory h WHERE h.changedBy = ?1")
    List<PatientCardsHistory> findByChangedBy(String changedBy);
}
