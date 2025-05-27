package org.dariaob.repositories;

import jakarta.transaction.Transactional;
import org.dariaob.models.Specializations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий специализаций
 */
@Repository
public interface SpecializationsRepository extends JpaRepository<Specializations, Long> {

    /**
     * Найти активную специализацию по ID
     * @param id ID специализации
     * @return Optional со специализацией, если найдена и не удалена
     */
    @Query("SELECT s FROM Specializations s WHERE s.id = ?1 AND s.isDeleted = false")
    Optional<Specializations> findActiveById(Long id);

    /**
     * Получить все активные специализации
     * @return список активных специализаций
     */
    @Query("SELECT s FROM Specializations s WHERE s.isDeleted = false")
    List<Specializations> findAllActive();

    /**
     * Найти специализацию по точному названию (без учета регистра)
     * @param name название специализации
     * @return Optional с найденной специализацией
     */
    Optional<Specializations> findFirstByNameIgnoreCaseAndIsDeletedFalseOrderByIdDesc(String name);

    /**
     * Мягкое удаление специализации
     * @param id ID специализации
     */
    @Transactional
    @Modifying
    @Query("UPDATE Specializations s SET s.isDeleted = true WHERE s.id = ?1")
    void softDelete(Long id);

    /**
     * Восстановить специализацию
     * @param id ID специализации
     */
    @Transactional
    @Modifying
    @Query("UPDATE Specializations s SET s.isDeleted = false WHERE s.id = ?1")
    void restore(Long id);

    /**
     * Поиск специализаций по части названия (без учета регистра)
     * @param namePart часть названия специализации
     * @return список подходящих специализаций
     */
    @Query("SELECT s FROM Specializations s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', ?1, '%')) AND s.isDeleted = false ORDER BY s.id DESC")
    List<Specializations> searchByName(String namePart);
}
