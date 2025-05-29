package org.dariaob.repository_tests;

import jakarta.transaction.Transactional;
import org.dariaob.TestWithContainer;
import org.dariaob.models.Doctors;
import org.dariaob.models.Offices;
import org.dariaob.repositories.DoctorsRepository;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * The type Doctors repository test.
 */
@DataJpaTest
@ExtendWith(SpringExtension.class)
@TestPropertySource("classpath:application-test.properties")
public class DoctorsRepositoryTest extends TestWithContainer {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private DoctorsRepository repository;

    private Long activeDoctorId1;
    private Long activeDoctorId2;
    private Long deletedDoctorId;
    private String phone1;
    private String phone2;
    private String phone3;

    /**
     * Sets up.
     */
    @BeforeEach
    public void setUp() {
        // Уникальные номера телефонов, чтобы не нарушать уникальность
        String suffix = String.valueOf(System.nanoTime()).substring(10);
        phone1 = "+7900000" + suffix;
        phone2 = "+7900001" + suffix;
        phone3 = "+7900002" + suffix;

        Offices office1 = createOffice("Терапевтический кабинет №1", false);
        entityManager.persist(office1);

        Doctors doctor1 = createDoctor("Иванов Петр Сергеевич", phone1,
                LocalDateTime.now().withHour(8).withMinute(0),
                LocalDateTime.now().withHour(16).withMinute(0),
                office1, false);

        Doctors doctor2 = createDoctor("Смирнова Елена Владимировна", phone2,
                LocalDateTime.now().withHour(9).withMinute(0),
                LocalDateTime.now().withHour(17).withMinute(0),
                office1, false);

        Doctors doctor3 = createDoctor("Архивный Врач", phone3,
                LocalDateTime.now().withHour(10).withMinute(0),
                LocalDateTime.now().withHour(18).withMinute(0),
                office1, true);

        entityManager.persist(doctor1);
        entityManager.persist(doctor2);
        entityManager.persist(doctor3);
        entityManager.flush();

        this.activeDoctorId1 = doctor1.getId();
        this.activeDoctorId2 = doctor2.getId();
        this.deletedDoctorId = doctor3.getId();
    }

    private Offices createOffice(String name, boolean isDeleted) {
        Offices office = new Offices();
        office.setName(name);
        office.setDeleted(isDeleted);
        return office;
    }

    private Doctors createDoctor(String name, String phone,
                                 LocalDateTime workFrom, LocalDateTime workTo,
                                 Offices office, boolean isDeleted) {
        Doctors doctor = new Doctors();
        doctor.setName(name);
        doctor.setPhone(phone);
        doctor.setWorkHoursFrom(workFrom);
        doctor.setWorkHoursFor(workTo);
        doctor.setOffice(office);
        doctor.setDeleted(isDeleted);
        return doctor;
    }

    /**
     * Find all active test.
     */
    @Test
    @DisplayName("Doctors - Repository - Find all active test")
    public void findAllActiveTest() {
        List<Doctors> doctors = repository.findAllActive();

        assertThat(doctors.stream().allMatch(d -> !d.isDeleted()), is(true));
        assertThat(doctors.stream().map(Doctors::getName).toList(),
                hasItems("Иванов Петр Сергеевич", "Смирнова Елена Владимировна"));
    }

    /**
     * Find active by id test.
     */
    @Test
    @DisplayName("Doctors - Repository - Find active by id test")
    public void findActiveByIdTest() {
        testFindActiveById(activeDoctorId1, "Иванов");
        testFindActiveById(activeDoctorId2, "Смирнова");

        Optional<Doctors> deletedDoctor = repository.findActiveById(deletedDoctorId);
        assertThat("Удаленный врач не должен находиться",
                deletedDoctor.isPresent(), is(false));
    }

    private void testFindActiveById(Long id, String namePrefix) {
        Optional<Doctors> doctor = repository.findActiveById(id);
        assertThat("Врач должен присутствовать", doctor.isPresent(), is(true));
        assertThat("Имя должно начинаться с " + namePrefix,
                doctor.get().getName(), startsWith(namePrefix));
        assertThat("Врач должен быть активным", doctor.get().isDeleted(), is(false));
    }

    /**
     * Find active by phone test.
     */
    @Test
    @DisplayName("Doctors - Repository - Find active by phone test")
    public void findActiveByPhoneTest() {
        Optional<Doctors> doctor = repository.findActiveByPhone(phone1);
        assertThat(doctor.isPresent(), is(true));
        assertThat(doctor.get().getId(), equalTo(activeDoctorId1));
    }

    /**
     * Soft delete test.
     */
    @Test
    @DisplayName("Doctors - Repository - Soft delete test")
    @Transactional
    public void softDeleteTest() {
        repository.softDelete(activeDoctorId1);
        entityManager.flush();
        entityManager.clear();

        Doctors deletedDoctor = entityManager.find(Doctors.class, activeDoctorId1);
        assertThat(deletedDoctor, notNullValue());
        assertThat(deletedDoctor.isDeleted(), is(true));

        List<Doctors> activeDoctors = repository.findAllActive();
        List<Long> activeIds = activeDoctors.stream().map(Doctors::getId).toList();

        assertThat(activeIds, not(hasItem(activeDoctorId1)));
        assertThat(activeIds, hasItem(activeDoctorId2));
    }

    /**
     * Restore test.
     */
    @Test
    @DisplayName("Doctors - Repository - Restore test")
    @Transactional
    public void restoreTest() {
        repository.softDelete(activeDoctorId1);
        repository.restore(activeDoctorId1);
        entityManager.flush();
        entityManager.clear();

        Doctors restored = entityManager.find(Doctors.class, activeDoctorId1);
        assertThat(restored, notNullValue());
        assertThat(restored.isDeleted(), is(false));

        Optional<Doctors> activeDoctor = repository.findActiveById(activeDoctorId1);
        assertThat(activeDoctor.isPresent(), is(true));
    }

    /**
     * Find all includes deleted.
     */
    @Test
    @DisplayName("Doctors - Repository - Standard find all includes deleted")
    public void findAllIncludesDeleted() {
        List<Doctors> allDoctors = repository.findAll();

        List<Long> allIds = allDoctors.stream().map(Doctors::getId).toList();
        assertThat(allIds, hasItems(activeDoctorId1, activeDoctorId2, deletedDoctorId));

        Map<Long, Boolean> doctorDeletionStatus = allDoctors.stream()
                .collect(Collectors.toMap(Doctors::getId, Doctors::isDeleted));

        assertThat(doctorDeletionStatus.get(activeDoctorId1), is(false));
        assertThat(doctorDeletionStatus.get(activeDoctorId2), is(false));
        assertThat(doctorDeletionStatus.get(deletedDoctorId), is(true));
    }
}
