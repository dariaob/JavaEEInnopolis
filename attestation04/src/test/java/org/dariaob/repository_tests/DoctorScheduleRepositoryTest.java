package org.dariaob.repository_tests;

import jakarta.transaction.Transactional;
import org.dariaob.TestWithContainer;
import org.dariaob.models.DoctorSchedule;
import org.dariaob.models.Doctors;
import org.dariaob.models.Offices;
import org.dariaob.repositories.DoctorScheduleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * The type Doctor schedule repository test.
 */
@DataJpaTest
@ExtendWith(SpringExtension.class)
@TestPropertySource("classpath:application-test.properties")
public class DoctorScheduleRepositoryTest extends TestWithContainer {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private DoctorScheduleRepository repository;

    private Long scheduleId1;
    private Long scheduleId2;
    private Long doctorId;

    /**
     * Sets up.
     */
    @BeforeEach
    public void setUp() {
        Offices office = new Offices();
        office.setName("Кабинет 101");
        office.setDeleted(false);
        entityManager.persist(office);

        Doctors doctor = new Doctors();
        doctor.setName("Доктор Кто");
        doctor.setPhone("+70000000000");
        doctor.setOffice(office);
        doctor.setDeleted(false);
        doctor.setWorkHoursFor(LocalDateTime.now().plusHours(8));
        doctor.setWorkHoursFrom(LocalDateTime.now());
        entityManager.persist(doctor);
        doctorId = doctor.getId();

        DoctorSchedule schedule1 = new DoctorSchedule();
        schedule1.setDoctor(doctor);
        schedule1.setDayOfWeek((short) 1); // понедельник
        schedule1.setStartTime(LocalTime.of(9, 0));
        schedule1.setEndTime(LocalTime.of(12, 0));
        schedule1.setOffice(office);
        schedule1.setDeleted(false);
        entityManager.persist(schedule1);
        scheduleId1 = schedule1.getId();

        DoctorSchedule schedule2 = new DoctorSchedule();
        schedule2.setDoctor(doctor);
        schedule2.setDayOfWeek((short) 2); // вторник
        schedule2.setStartTime(LocalTime.of(13, 0));
        schedule2.setEndTime(LocalTime.of(16, 0));
        schedule2.setOffice(office);
        schedule2.setDeleted(false);
        entityManager.persist(schedule2);
        scheduleId2 = schedule2.getId();

        entityManager.flush();
    }

    /**
     * Find all active test.
     */
    @Test
    @DisplayName("DoctorSchedule - Repository - Find all active test")
    public void findAllActiveTest() {
        List<DoctorSchedule> schedules = repository.findAllActive();
        assertThat(schedules, not(empty()));
        assertThat(schedules.stream().allMatch(s -> !s.isDeleted()), is(true));
    }

    /**
     * Find active by id test.
     */
    @Test
    @DisplayName("DoctorSchedule - Repository - Find active by ID test")
    public void findActiveByIdTest() {
        Optional<DoctorSchedule> schedule = repository.findActiveById(scheduleId1);
        assertThat(schedule.isPresent(), is(true));
        assertThat(schedule.get().getId(), equalTo(scheduleId1));
        assertThat(schedule.get().isDeleted(), is(false));
    }

    /**
     * Find by doctor test.
     */
    @Test
    @DisplayName("DoctorSchedule - Repository - Find by doctor test")
    public void findByDoctorTest() {
        List<DoctorSchedule> schedules = repository.findByDoctor(doctorId);
        assertThat(schedules, not(empty()));
        assertThat(schedules.stream().allMatch(s -> s.getDoctor().getId().equals(doctorId) && !s.isDeleted()), is(true));
    }

    /**
     * Find by doctor and day test.
     */
    @Test
    @DisplayName("DoctorSchedule - Repository - Find by doctor and day test")
    public void findByDoctorAndDayTest() {
        List<DoctorSchedule> schedules = repository.findByDoctorAndDay(doctorId, (short) 1);
        assertThat(schedules, not(empty()));
        assertThat(schedules.stream()
                .allMatch(s -> s.getDoctor().getId().equals(doctorId)
                        && s.getDayOfWeek() == 1
                        && !s.isDeleted()), is(true));
    }

    /**
     * Soft delete test.
     */
    @Test
    @DisplayName("DoctorSchedule - Repository - Soft delete test")
    @Transactional
    public void softDeleteTest() {
        repository.softDelete(scheduleId1);
        entityManager.flush();

        Optional<DoctorSchedule> schedule = repository.findActiveById(scheduleId1);
        assertThat(schedule.isPresent(), is(false));
    }

    /**
     * Restore test.
     */
    @Test
    @DisplayName("DoctorSchedule - Repository - Restore test")
    @Transactional
    public void restoreTest() {
        repository.softDelete(scheduleId2);
        entityManager.flush();

        repository.restore(scheduleId2);
        entityManager.flush();

        Optional<DoctorSchedule> schedule = repository.findActiveById(scheduleId2);
        assertThat(schedule.isPresent(), is(true));
        assertThat(schedule.get().isDeleted(), is(false));
    }
}
