package org.dariaob.repositories;

import jakarta.transaction.Transactional;
import org.dariaob.models.DoctorSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с расписанием врачей.
 */
@Repository
public interface DoctorScheduleRepository extends JpaRepository<DoctorSchedule, Long> {

    /**
     * Получить все активные записи расписания.
     *
     * @return список активных записей расписания
     */
    @Query("SELECT s FROM DoctorSchedule s WHERE s.isDeleted = false")
    List<DoctorSchedule> findAllActive();

    /**
     * Получить активную запись расписания по идентификатору.
     *
     * @param id идентификатор записи
     * @return Optional с записью расписания, если она активна
     */
    @Query("SELECT s FROM DoctorSchedule s WHERE s.id = :id AND s.isDeleted = false")
    Optional<DoctorSchedule> findActiveById(Long id);

    /**
     * Получить все активные записи расписания по идентификатору врача.
     *
     * @param doctorId идентификатор врача
     * @return список активных записей расписания врача
     */
    @Query("SELECT s FROM DoctorSchedule s WHERE s.doctor.id = :doctorId AND s.isDeleted = false")
    List<DoctorSchedule> findByDoctor(Long doctorId);

    /**
     * Получить расписание врача по дню недели (только активные записи).
     *
     * @param doctorId идентификатор врача
     * @param day      день недели (1-7)
     * @return список активных записей расписания врача на указанный день
     */
    @Query("SELECT s FROM DoctorSchedule s WHERE s.doctor.id = :doctorId AND s.dayOfWeek = :day AND s.isDeleted = false")
    List<DoctorSchedule> findByDoctorAndDay(Long doctorId, Short day);

    /**
     * Мягко удалить запись расписания по идентификатору.
     *
     * @param id идентификатор записи
     */
    @Modifying
    @Transactional
    @Query("UPDATE DoctorSchedule s SET s.isDeleted = true WHERE s.id = :id")
    void softDelete(Long id);

    /**
     * Восстановить ранее удалённую запись расписания по идентификатору.
     *
     * @param id идентификатор записи
     */
    @Modifying
    @Transactional
    @Query("UPDATE DoctorSchedule s SET s.isDeleted = false WHERE s.id = :id")
    void restore(Long id);
}
