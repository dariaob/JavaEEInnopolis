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

@Repository
public interface OfficesRepository extends JpaRepository<Offices, Long> {

    // 1. Получить только активные офисы (где isDeleted = false)
    @Query("SELECT o FROM Offices o WHERE o.isDeleted = false")
    List<Offices> findAllActive();

    // 2. Найти активный офис по ID
    @Query("SELECT o FROM Offices o WHERE o.id = ?1 AND o.isDeleted = false")
    Optional<Offices> findActiveById(Long id);

    // 3. Удалить офис (установить isDeleted = true)
    @Modifying
    @Query("UPDATE Offices o SET o.isDeleted = true WHERE o.id = :id")
    void softDelete(@Param("id") Long id);

    /**
     * 4. Восстановить офис (установить isDeleted = false)
     */
    @Transactional
    @Modifying
    @Query("UPDATE Offices o SET o.isDeleted = false WHERE o.id = ?1")
    void restore(Long id);

    /**
     * 5. Мягко удаляем все офисы
     */
    @Transactional
    @Modifying
    @Query("UPDATE Offices o SET o.isDeleted = true WHERE o.isDeleted = false")
    void softDeleteAll();
}
