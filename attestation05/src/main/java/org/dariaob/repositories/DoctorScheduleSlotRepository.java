package org.dariaob.repositories;

import org.dariaob.models.DoctorScheduleSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для доступа к слотам расписания врачей (DoctorScheduleSlot).
 * Предоставляет методы поиска по расписанию, врачу, офису, дню недели, приёму и флагам isDeleted / isBooked.
 */
@Repository
public interface DoctorScheduleSlotRepository extends JpaRepository<DoctorScheduleSlot, Long> {

    /**
     * Возвращает список всех активных (неудалённых) слотов по ID расписания.
     *
     * @param doctorScheduleId ID расписания врача
     * @return список слотов без признака удаления
     */
    List<DoctorScheduleSlot> findByDoctorScheduleIdAndIsDeletedFalse(Long doctorScheduleId);

    /**
     * Возвращает список всех свободных и неудалённых слотов по врачу в заданном временном диапазоне.
     *
     * @param doctorId ID врача
     * @param start    начало временного диапазона (включительно)
     * @param end      конец временного диапазона (включительно)
     * @return список доступных слотов
     */
    List<DoctorScheduleSlot> findByDoctorScheduleDoctorIdAndStartTimeBetweenAndIsDeletedFalseAndIsBookedFalse(
            Long doctorId,
            LocalDateTime start,
            LocalDateTime end
    );

    /**
     * Проверяет наличие активного (неудалённого) слота по ID расписания и временным границам.
     * Используется для предотвращения дублирования слотов.
     *
     * @param scheduleId ID расписания
     * @param from       время начала слота
     * @param to         время окончания слота
     * @return true, если такой слот уже существует
     */
    boolean existsByDoctorScheduleIdAndStartTimeAndEndTimeAndIsDeletedFalse(
            Long scheduleId,
            LocalDateTime from,
            LocalDateTime to
    );

    /**
     * Возвращает список всех неудалённых слотов по ID офиса.
     *
     * @param officeId ID офиса
     * @return список слотов, привязанных к офису
     */
    List<DoctorScheduleSlot> findByDoctorScheduleDoctorOfficeIdAndIsDeletedFalse(Long officeId);

    /**
     * Возвращает список всех неудалённых слотов по дню недели.
     *
     * @param dayOfWeek день недели (например, MONDAY)
     * @return список слотов, соответствующих дню недели
     */
    List<DoctorScheduleSlot> findByDoctorScheduleDayOfWeekAndIsDeletedFalse(Short dayOfWeek);

    /**
     * Возвращает список всех неудалённых слотов, связанных с конкретным приёмом.
     *
     * @param appointmentId ID приёма
     * @return список слотов, привязанных к приёму
     */
    List<DoctorScheduleSlot> findByAppointmentIdAndIsDeletedFalse(Long appointmentId);

    /**
     * Находит первый свободный слот для указанного врача в заданный временной интервал.
     *
     * @param doctorId ID врача
     * @param start    начало временного интервала (включительно)
     * @param end      конец временного интервала (включительно)
     * @return Optional с найденным слотом или empty, если слот не найден
     */
    @Query("SELECT s FROM DoctorScheduleSlot s WHERE " +
            "s.doctorSchedule.doctor.id = :doctorId AND " +
            "s.startTime = :start AND " +
            "s.endTime = :end AND " +
            "s.isBooked = false AND " +
            "s.isDeleted = false")
    Optional<DoctorScheduleSlot> findFirstByDoctorIdAndTime(
            @Param("doctorId") Long doctorId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    /**
     * Проверяет существование слотов для указанного врача на конкретную дату.
     *
     * @param doctorId ID врача
     * @param date     дата для проверки
     * @return true если слоты существуют, false в противном случае
     */
    @Query("SELECT COUNT(s) > 0 FROM DoctorScheduleSlot s WHERE " +
            "s.doctorSchedule.doctor.id = :doctorId AND " +
            "CAST(s.startTime AS localdate) = :date")
    boolean existsByDoctorIdAndDate(
            @Param("doctorId") Long doctorId,
            @Param("date") LocalDate date
    );
}