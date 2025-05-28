package org.dariaob.repositories;

import jakarta.transaction.Transactional;
import org.dariaob.models.Doctors;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с врачами.
 */
@Repository
public interface DoctorsRepository extends JpaRepository<Doctors, Long> {

    /**
     * Получить все активные записи о врачах.
     * Выбирает записи, где isDeleted = false.
     *
     * @return список активных врачей
     */
    @Query("SELECT d FROM Doctors d WHERE d.isDeleted = false")
    List<Doctors> findAllActive();

    /**
     * Получить активного врача по ID.
     * Выбирает врача по ID, где isDeleted = false.
     *
     * @param id идентификатор врача
     * @return объект врача в виде Optional
     */
    @Query("SELECT d FROM Doctors d WHERE d.id = :id AND d.isDeleted = false")
    Optional<Doctors> findActiveById(Long id);

    /**
     * Получить активного врача по телефону.
     * Выбирает врача по телефону, где isDeleted = false.
     *
     * @param phone телефон врача
     * @return объект врача в виде Optional
     */
    @Query("SELECT d FROM Doctors d WHERE d.phone = :phone AND d.isDeleted = false")
    Optional<Doctors> findActiveByPhone(String phone);

    /**
     * Мягкое удаление врача.
     * Устанавливает флаг isDeleted в true для записи.
     *
     * @param id идентификатор врача
     */
    @Modifying
    @Transactional
    @Query("UPDATE Doctors d SET d.isDeleted = true WHERE d.id = :id")
    void softDelete(Long id);

    /**
     * Восстановление удалённого врача (установить isDeleted = false).
     *
     * @param id идентификатор врача
     */
    @Transactional
    @Modifying
    @Query("UPDATE Doctors d SET d.isDeleted = false WHERE d.id = :id")
    void restore(Long id);
}
