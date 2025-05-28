package org.dariaob.repositories;

import org.dariaob.models.Offices;
import org.dariaob.models.Patients;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientsRepository extends JpaRepository<Patients, Long> {
    // 1. Получить всех активных пациентов
    @Query("SELECT p FROM Patients p WHERE p.isDeleted = false")
    List<Patients> findAllActive();

    // 2. Найти активного пациента по ID
    @Query("SELECT p FROM Patients p WHERE p.id = ?1 AND p.isDeleted = false")
    Optional<Patients> findActiveById(Long id);

    // 3. Мягкое удаление (isDeleted = true)
    @Transactional
    @Modifying
    @Query("UPDATE Patients p SET p.isDeleted = true WHERE p.id = ?1")
    void softDelete(Long id);

    // 4. Восстановить пациента (isDeleted = false)
    @Transactional
    @Modifying
    @Query("UPDATE Patients p SET p.isDeleted = false WHERE p.id = ?1")
    void restore(Long id);

    // 5. Поиск по номеру телефона (только активные)
    @Query("SELECT p FROM Patients p WHERE p.phone = ?1 AND p.isDeleted = false")
    Optional<Patients> findByPhone(String phone);
}
