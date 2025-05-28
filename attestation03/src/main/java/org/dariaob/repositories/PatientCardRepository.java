package org.dariaob.repositories;

import jakarta.transaction.Transactional;
import org.dariaob.models.PatientCards;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с карточками пациентов
 */
@Repository
public interface PatientCardRepository extends JpaRepository<PatientCards, Long> {

    /**
     * Найти активную карту пациента по ID
     * @param id ID карты пациента
     * @return Optional с картой пациента, если найдена и не удалена
     */
    @Query("SELECT pc FROM PatientCards pc WHERE pc.id = ?1 AND pc.isDeleted = false")
    Optional<PatientCards> findActiveById(Long id);

    /**
     * Найти все активные карты пациентов
     * @return список активных карт пациентов
     */
    @Query("SELECT pc FROM PatientCards pc WHERE pc.isDeleted = false")
    List<PatientCards> findAllActive();

    /**
     * Мягкое удаление карты пациента
     * @param id ID карты пациента
     */
    @Transactional
    @Modifying
    @Query("UPDATE PatientCards pc SET pc.isDeleted = true WHERE pc.id = ?1")
    void softDelete(Long id);

    /**
     * Восстановить карту пациента
     * @param id ID карты пациента
     */
    @Transactional
    @Modifying
    @Query("UPDATE PatientCards pc SET pc.isDeleted = false WHERE pc.id = ?1")
    void restore(Long id);

    /**
     * Найти карту по ID пациента
     * @param patientId ID пациента
     * @return Optional с картой пациента, если найдена
     */
    @Query("SELECT pc FROM PatientCards pc WHERE pc.patient.id = ?1 AND pc.isDeleted = false")
    Optional<PatientCards> findByPatientId(Long patientId);

    /**
     * Найти карты по диагнозу (LIKE поиск)
     * @param diagnosis часть диагноза для поиска
     * @return список подходящих карт пациентов
     */
    @Query("SELECT p FROM PatientCards p WHERE lower(p.diagnosis) LIKE lower(concat('%', :diagnosis,'%'))")
    List<PatientCards> findByDiagnosisContainingIgnoreCase(@Param("diagnosis") String diagnosis);

}
