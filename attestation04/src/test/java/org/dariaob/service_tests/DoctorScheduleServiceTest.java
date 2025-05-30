package org.dariaob.service_tests;

import org.dariaob.models.DoctorSchedule;
import org.dariaob.models.Doctors;
import org.dariaob.models.Offices;
import org.dariaob.repositories.DoctorScheduleRepository;
import org.dariaob.services.DoctorScheduleService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * The type Doctor schedule service test.
 */
@ExtendWith(MockitoExtension.class)
public class DoctorScheduleServiceTest {

    @Mock
    private DoctorScheduleRepository repository;

    @InjectMocks
    private DoctorScheduleService service;

    private Doctors createTestDoctor(Long id) {
        Doctors doctor = new Doctors();
        doctor.setId(id);
        doctor.setName("Dr. Test");
        return doctor;
    }

    private Offices createTestOffice(Long id) {
        Offices office = new Offices();
        office.setId(id);
        office.setName("A101");
        return office;
    }

    private DoctorSchedule createTestSchedule(Long id, Doctors doctor, Short dayOfWeek,
                                              String startTime, String endTime, Offices office) {
        DoctorSchedule schedule = new DoctorSchedule();
        schedule.setId(id);
        schedule.setDoctor(doctor);
        schedule.setDayOfWeek(dayOfWeek);
        schedule.setStartTime(LocalTime.parse(startTime));
        schedule.setEndTime(LocalTime.parse(endTime));
        schedule.setOffice(office);
        schedule.setDeleted(false);
        return schedule;
    }

    /**
     * Doctor schedule get all active test.
     */
    @Test
    @DisplayName("DoctorSchedule - Service - Get all active schedules test")
    public void doctorScheduleGetAllActiveTest() {
        Doctors doctor = createTestDoctor(1L);
        Offices office = createTestOffice(1L);
        DoctorSchedule schedule1 = createTestSchedule(1L, doctor, (short) 1, "09:00", "17:00", office);
        DoctorSchedule schedule2 = createTestSchedule(2L, doctor, (short) 2, "10:00", "18:00", office);

        when(repository.findAllActive()).thenReturn(List.of(schedule1, schedule2));

        List<DoctorSchedule> result = service.getAllActive();

        assertThat(result, hasSize(2));
        assertThat(result.get(0).getDoctor().getName(), equalTo("Dr. Test"));
        verify(repository).findAllActive();
    }

    /**
     * Doctor schedule get by id found test.
     */
    @Test
    @DisplayName("DoctorSchedule - Service - Get schedule by ID test - Found")
    public void doctorScheduleGetByIdFoundTest() {
        Doctors doctor = createTestDoctor(1L);
        DoctorSchedule schedule = createTestSchedule(1L, doctor, (short) 1, "09:00", "17:00", null);
        when(repository.findActiveById(1L)).thenReturn(Optional.of(schedule));

        Optional<DoctorSchedule> result = service.getActiveById(1L);

        assertThat(result.isPresent(), is(true));
        assertThat(result.get().getDayOfWeek(), equalTo((short) 1));
    }

    /**
     * Doctor schedule get by doctor test.
     */
    @Test
    @DisplayName("DoctorSchedule - Service - Get schedules by doctor test")
    public void doctorScheduleGetByDoctorTest() {
        Doctors doctor = createTestDoctor(1L);
        DoctorSchedule schedule = createTestSchedule(1L, doctor, (short) 1, "09:00", "17:00", null);
        when(repository.findByDoctor(1L)).thenReturn(List.of(schedule));

        List<DoctorSchedule> result = service.getByDoctor(1L);

        assertThat(result, hasSize(1));
        assertThat(result.get(0).getDoctor().getId(), equalTo(1L));
    }

    /**
     * Doctor schedule get by doctor and day test.
     */
    @Test
    @DisplayName("DoctorSchedule - Service - Get schedules by doctor and day test")
    public void doctorScheduleGetByDoctorAndDayTest() {
        Doctors doctor = createTestDoctor(1L);
        DoctorSchedule schedule = createTestSchedule(1L, doctor, (short) 1, "09:00", "17:00", null);
        when(repository.findByDoctorAndDay(1L, (short) 1)).thenReturn(List.of(schedule));

        List<DoctorSchedule> result = service.getByDoctorAndDay(1L, (short) 1);

        assertThat(result, hasSize(1));
        assertThat(result.get(0).getStartTime(), equalTo(LocalTime.of(9, 0)));
    }

    /**
     * Doctor schedule create test.
     */
    @Test
    @DisplayName("DoctorSchedule - Service - Create schedule test")
    public void doctorScheduleCreateTest() {
        Doctors doctor = createTestDoctor(1L);
        Offices office = createTestOffice(1L);
        DoctorSchedule newSchedule = createTestSchedule(null, doctor, (short) 1, "09:00", "17:00", office);
        DoctorSchedule savedSchedule = createTestSchedule(1L, doctor, (short) 1, "09:00", "17:00", office);

        when(repository.save(newSchedule)).thenReturn(savedSchedule);

        DoctorSchedule result = service.create(newSchedule);

        assertThat(result.getId(), equalTo(1L));
        assertThat(result.getOffice().getName(), equalTo("A101"));
    }

    /**
     * Doctor schedule update success test.
     */
    @Test
    @DisplayName("DoctorSchedule - Service - Update schedule test - Success")
    public void doctorScheduleUpdateSuccessTest() {
        Doctors doctor = createTestDoctor(1L);
        Offices office = createTestOffice(1L);
        DoctorSchedule existing = createTestSchedule(1L, doctor, (short) 1, "09:00", "17:00", office);
        DoctorSchedule updated = createTestSchedule(1L, doctor, (short) 2, "10:00", "18:00", office);

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(updated);

        DoctorSchedule result = service.update(1L, updated);

        assertThat(result.getDayOfWeek(), equalTo((short) 2));
        assertThat(result.getEndTime(), equalTo(LocalTime.of(18, 0)));
    }

    /**
     * Doctor schedule update not found test.
     */
    @Test
    @DisplayName("DoctorSchedule - Service - Update schedule test - Not found")
    public void doctorScheduleUpdateNotFoundTest() {
        Doctors doctor = createTestDoctor(1L);
        DoctorSchedule updated = createTestSchedule(1L, doctor, (short) 2, "10:00", "18:00", null);
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.update(1L, updated));
    }

    /**
     * Doctor schedule soft delete test.
     */
    @Test
    @DisplayName("DoctorSchedule - Service - Soft delete schedule test")
    public void doctorScheduleSoftDeleteTest() {
        doNothing().when(repository).softDelete(1L);

        service.delete(1L);

        verify(repository).softDelete(1L);
    }

    /**
     * Doctor schedule restore test.
     */
    @Test
    @DisplayName("DoctorSchedule - Service - Restore schedule test")
    public void doctorScheduleRestoreTest() {
        doNothing().when(repository).restore(1L);

        service.restore(1L);

        verify(repository).restore(1L);
    }
}
