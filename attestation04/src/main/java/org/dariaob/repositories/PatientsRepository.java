package org.dariaob.repositories;

import org.dariaob.models.Patients;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с пациентами клиники.
 */
@Repository
public interface PatientsRepository extends JpaRepository<Patients, Long> {

    /**
     * Получить список всех активных пациентов (где isDeleted = false).
     *
     * @return список активных пациентов
     */
    @Query("SELECT p FROM Patients p WHERE p.isDeleted = false")
    List<Patients> findAllActive();

    /**
     * Найти активного пациента по его идентификатору.
     *
     * @param id идентификатор пациента
     * @return Optional с пациентом, если он активен
     */
    @Query("SELECT p FROM Patients p WHERE p.id = ?1 AND p.isDeleted = false")
    Optional<Patients> findActiveById(Long id);

    /**
     * Мягко удалить пациента, установив флаг isDeleted = true.
     *
     * @param id идентификатор пациента
     */
    @Transactional
    @Modifying
    @Query("UPDATE Patients p SET p.isDeleted = true WHERE p.id = ?1")
    void softDelete(Long id);

    /**
     * Восстановить ранее удалённого пациента, установив флаг isDeleted = false.
     *
     * @param id идентификатор пациента
     */
    @Transactional
    @Modifying
    @Query("UPDATE Patients p SET p.isDeleted = false WHERE p.id = ?1")
    void restore(Long id);

    /**
     * Найти активного пациента по номеру телефона.
     *
     * @param phone номер телефона пациента
     * @return Optional с пациентом, если он активен
     */
    @Query("SELECT p FROM Patients p WHERE p.phone = ?1 AND p.isDeleted = false")
    Optional<Patients> findByPhone(String phone);
}
