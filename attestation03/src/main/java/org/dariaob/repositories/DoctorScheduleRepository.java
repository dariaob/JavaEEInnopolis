package org.dariaob.repositories;

import jakarta.transaction.Transactional;
import org.dariaob.models.DoctorSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorScheduleRepository extends JpaRepository<DoctorSchedule, Long> {

    // 1. Получить все активные записи
    @Query("SELECT s FROM DoctorSchedule s WHERE s.isDeleted = false")
    List<DoctorSchedule> findAllActive();

    // 2. Получить активную запись по ID
    @Query("SELECT s FROM DoctorSchedule s WHERE s.id = :id AND s.isDeleted = false")
    Optional<DoctorSchedule> findActiveById(Long id);

    // 3. Получить все записи по врачу (только активные)
    @Query("SELECT s FROM DoctorSchedule s WHERE s.doctor.id = :doctorId AND s.isDeleted = false")
    List<DoctorSchedule> findByDoctor(Long doctorId);

    // 4. Получить расписание врача по дню недели (только активные)
    @Query("SELECT s FROM DoctorSchedule s WHERE s.doctor.id = :doctorId AND s.dayOfWeek = :day AND s.isDeleted = false")
    List<DoctorSchedule> findByDoctorAndDay(Long doctorId, Short day);

    // 5. Soft delete по ID
    @Modifying
    @Transactional
    @Query("UPDATE DoctorSchedule s SET s.isDeleted = true WHERE s.id = :id")
    void softDelete(Long id);

    // 6. Восстановление записи
    @Modifying
    @Transactional
    @Query("UPDATE DoctorSchedule s SET s.isDeleted = false WHERE s.id = :id")
    void restore(Long id);
}
