package org.dariaob.repositories;

import org.dariaob.models.Offices;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с офисами клиники.
 */
@Repository
public interface OfficesRepository extends JpaRepository<Offices, Long> {

    /**
     * Получить список всех активных офисов (где isDeleted = false).
     *
     * @return список активных офисов
     */
    @Query("SELECT o FROM Offices o WHERE o.isDeleted = false")
    List<Offices> findAllActive();

    /**
     * Найти активный офис по его идентификатору.
     *
     * @param id идентификатор офиса
     * @return Optional с офисом, если он активен
     */
    @Query("SELECT o FROM Offices o WHERE o.id = ?1 AND o.isDeleted = false")
    Optional<Offices> findActiveById(Long id);

    /**
     * Мягко удалить офис, установив флаг isDeleted = true.
     *
     * @param id идентификатор офиса
     */
    @Modifying
    @Query("UPDATE Offices o SET o.isDeleted = true WHERE o.id = :id")
    void softDelete(@Param("id") Long id);

    /**
     * Восстановить ранее удалённый офис, установив флаг isDeleted = false.
     *
     * @param id идентификатор офиса
     */
    @Transactional
    @Modifying
    @Query("UPDATE Offices o SET o.isDeleted = false WHERE o.id = ?1")
    void restore(Long id);

    /**
     * Мягко удалить все активные офисы (установить isDeleted = true для всех).
     */
    @Transactional
    @Modifying
    @Query("UPDATE Offices o SET o.isDeleted = true WHERE o.isDeleted = false")
    void softDeleteAll();
}
