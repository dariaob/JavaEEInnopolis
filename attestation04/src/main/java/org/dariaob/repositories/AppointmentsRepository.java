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
 * Репозиторий для работы с приёмами.
 */
@Repository
public interface AppointmentsRepository extends JpaRepository<Appointments, Long> {

    /**
     * Получить список всех активных приёмов (не удалённых).
     *
     * @return список активных приёмов
     */
    @Query("SELECT a FROM Appointments a WHERE a.isDeleted = false")
    List<Appointments> findAllActive();

    /**
     * Найти активный приём по идентификатору.
     *
     * @param id идентификатор приёма
     * @return приём, если найден и не удалён, иначе пустой Optional
     */
    @Query("SELECT a FROM Appointments a WHERE a.id = ?1 AND a.isDeleted = false")
    Optional<Appointments> findActiveById(Long id);

    /**
     * Мягкое удаление приёма по идентификатору.
     *
     * @param id идентификатор приёма
     */
    @Transactional
    @Modifying
    @Query("UPDATE Appointments a SET a.isDeleted = true WHERE a.id = ?1")
    void softDelete(Long id);

    /**
     * Восстановить ранее удалённый приём по идентификатору.
     *
     * @param id идентификатор приёма
     */
    @Transactional
    @Modifying
    @Query("UPDATE Appointments a SET a.isDeleted = false WHERE a.id = ?1")
    void restore(Long id);

    /**
     * Получить список всех активных приёмов по идентификатору врача.
     *
     * @param doctorId идентификатор врача
     * @return список активных приёмов врача
     */
    @Query("SELECT a FROM Appointments a WHERE a.doctor.id = ?1 AND a.isDeleted = false")
    List<Appointments> findAllActiveByDoctorId(Long doctorId);

    /**
     * Получить список всех активных приёмов по идентификатору пациента.
     *
     * @param patientId идентификатор пациента
     * @return список активных приёмов пациента
     */
    @Query("SELECT a FROM Appointments a WHERE a.patient.id = ?1 AND a.isDeleted = false")
    List<Appointments> findAllActiveByPatientId(Long patientId);

    /**
     * Проверить, существует ли запись врача, пересекающаяся с указанным интервалом времени.
     *
     * @param doctorId идентификатор врача
     * @param from     начало интервала
     * @param to       конец интервала
     * @return true, если пересечение существует, иначе false
     */
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
