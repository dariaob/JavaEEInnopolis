package org.dariaob.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.dariaob.models.DoctorSchedule;
import org.dariaob.models.DoctorScheduleSlot;
import org.dariaob.repositories.DoctorScheduleRepository;
import org.dariaob.repositories.DoctorScheduleSlotRepository;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorScheduleSlotGeneratorService {

    private final DoctorScheduleRepository scheduleRepository;
    private final DoctorScheduleSlotRepository slotRepository;
    private static final int SLOT_DURATION = 30;

    /**
     * Генерация слотов на определённую дату.
     *
     * @param doctorId идентификатор врача
     * @param date     дата (LocalDate), для которой нужно сгенерировать слоты
     */
    @Transactional
    public void generateSlotsForDate(Long doctorId, LocalDate date) {
        short dayOfWeek = (short) date.getDayOfWeek().getValue(); // 1 = Понедельник, 7 = Воскресенье

        List<DoctorSchedule> schedules = scheduleRepository.findByDoctorAndDay(doctorId, dayOfWeek);
        if (schedules.isEmpty()) return;

        List<DoctorScheduleSlot> slotsToSave = new ArrayList<>();

        for (DoctorSchedule schedule : schedules) {
            LocalTime from = schedule.getStartTime();
            LocalTime to = schedule.getEndTime();

            LocalDateTime slotStart = LocalDateTime.of(date, from);

            while (!slotStart.toLocalTime().plusMinutes(SLOT_DURATION).isAfter(to)) {
                // Проверяем, не существует ли уже такой слот
                if (!slotRepository.existsByDoctorScheduleIdAndStartTimeAndEndTimeAndIsDeletedFalse(schedule.getId(), slotStart, slotStart.plusMinutes(SLOT_DURATION))) {
                    DoctorScheduleSlot slot = DoctorScheduleSlot.builder()
                            .doctorSchedule(schedule)
                            .startTime(slotStart)
                            .endTime(slotStart.plusMinutes(SLOT_DURATION))
                            .isBooked(false)
                            .isDeleted(false)
                            .build();
                    slotsToSave.add(slot);
                }
                slotStart = slotStart.plusMinutes(SLOT_DURATION);
            }

            slotRepository.saveAll(slotsToSave);
        }
    }
}

