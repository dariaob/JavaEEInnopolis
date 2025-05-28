package org.dariaob.repositories;

import jakarta.transaction.Transactional;
import org.dariaob.models.Appointments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с приёмами
 */
@Repository
public interface AppointmentsRepository extends JpaRepository<Appointments, Long> {

    // 1. Получить все активные приёмы (не удалённые)
    @Query("SELECT a FROM Appointments a WHERE a.isDeleted = false")
    List<Appointments> findAllActive();

    // 2. Найти активный приём по ID
    @Query("SELECT a FROM Appointments a WHERE a.id = ?1 AND a.isDeleted = false")
    Optional<Appointments> findActiveById(Long id);

    // 3. Мягкое удаление приёма
    @Transactional
    @Modifying
    @Query("UPDATE Appointments a SET a.isDeleted = true WHERE a.id = ?1")
    void softDelete(Long id);

    // 4. Восстановить ранее удалённый приём
    @Transactional
    @Modifying
    @Query("UPDATE Appointments a SET a.isDeleted = false WHERE a.id = ?1")
    void restore(Long id);

    // 5. Найти все активные приёмы по врачу
    @Query("SELECT a FROM Appointments a WHERE a.doctor.id = ?1 AND a.isDeleted = false")
    List<Appointments> findAllActiveByDoctorId(Long doctorId);

    // 6. Найти все активные приёмы по пациенту
    @Query("SELECT a FROM Appointments a WHERE a.patient.id = ?1 AND a.isDeleted = false")
    List<Appointments> findAllActiveByPatientId(Long patientId);

    // 7. Проверить наличие записи в заданный интервал времени (например, на конфликт времени)
    @Query("""
        SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END
        FROM Appointments a
        WHERE a.doctor.id = ?1
          AND a.isDeleted = false
          AND a.workHoursFrom < ?3
          AND a.workHoursFor > ?2
    """)
    boolean existsOverlappingAppointment(Long doctorId, LocalDateTime from, LocalDateTime to);
}
