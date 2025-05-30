package org.dariaob.repository_tests;

import jakarta.transaction.Transactional;
import org.dariaob.TestWithContainer;
import org.dariaob.models.*;
import org.dariaob.repositories.DoctorSpecializationsRepository;
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * The type Doctor specializations repository test.
 */
@DataJpaTest
@ExtendWith(SpringExtension.class)
@TestPropertySource("classpath:application-test.properties")
public class DoctorSpecializationsRepositoryTest extends TestWithContainer {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private DoctorSpecializationsRepository repository;

    private Long doctorId;
    private Long specializationId1 = 100L;
    private Long specializationId2 = 200L;

    /**
     * Sets up.
     */
    @BeforeEach
    public void setUp() {
        Offices office = new Offices();
        office.setName("Кабинета Хауса");
        office.setDeleted(false);
        entityManager.persist(office);

        Doctors doctor = new Doctors();
        doctor.setName("Доктор Хаус");
        doctor.setPhone("+79999999999");
        doctor.setWorkHoursFrom(LocalDateTime.of(2023, 1, 1, 8, 0));
        doctor.setWorkHoursFor(LocalDateTime.of(2023, 1, 1, 17, 0));
        doctor.setOffice(office);
        doctor.setDeleted(false);
        entityManager.persist(doctor);
        doctorId = doctor.getId();

        Specializations spec1 = new Specializations();
        spec1.setName("Невролог");
        spec1.setDeleted(false);
        entityManager.persist(spec1);
        entityManager.flush();
        specializationId1 = spec1.getId();

        Specializations spec2 = new Specializations();
        spec2.setName("Кардиолог");
        spec2.setDeleted(false);
        entityManager.persist(spec2);
        entityManager.flush();
        specializationId2 = spec2.getId();

        DoctorSpecializations ds1 = new DoctorSpecializations(
                new DoctorSpecializationId(doctorId, specializationId1), doctor, spec1);
        DoctorSpecializations ds2 = new DoctorSpecializations(
                new DoctorSpecializationId(doctorId, specializationId2), doctor, spec2);

        entityManager.persist(ds1);
        entityManager.persist(ds2);
        entityManager.flush();
    }

    /**
     * Exists by id doctor id and id specialization id test.
     */
    @Test
    @DisplayName("DoctorSpecializations - Repository - Exists by doctorId and specializationId test")
    public void existsByIdDoctorIdAndIdSpecializationIdTest() {
        boolean exists = repository.existsByIdDoctorIdAndIdSpecializationId(doctorId, specializationId1);
        assertThat(exists, is(true));

        boolean notExists = repository.existsByIdDoctorIdAndIdSpecializationId(999L, 888L);
        assertThat(notExists, is(false));
    }

    /**
     * Find all by doctor id test.
     */
    @Test
    @DisplayName("DoctorSpecializations - Repository - Find all by doctorId test")
    public void findAllByDoctorIdTest() {
        List<DoctorSpecializations> result = repository.findAllByIdDoctorId(doctorId);
        assertThat(result.stream()
                .anyMatch(ds -> ds.getId().getSpecializationId().equals(specializationId1)), is(true));
        assertThat(result.stream()
                .anyMatch(ds -> ds.getId().getSpecializationId().equals(specializationId2)), is(true));
    }

    /**
     * Find all by specialization id test.
     */
    @Test
    @DisplayName("DoctorSpecializations - Repository - Find all by specializationId test")
    public void findAllBySpecializationIdTest() {
        List<DoctorSpecializations> result = repository.findAllByIdSpecializationId(specializationId1);
        assertThat(result.stream()
                .anyMatch(ds -> ds.getId().getDoctorId().equals(doctorId)), is(true));
    }

    /**
     * Delete by doctor id and specialization id test.
     */
    @Test
    @DisplayName("DoctorSpecializations - Repository - Delete by doctorId and specializationId test")
    @Transactional
    public void deleteByDoctorIdAndSpecializationIdTest() {
        repository.deleteByDoctorIdAndSpecializationId(doctorId, specializationId1);
        entityManager.flush();

        boolean exists = repository.existsByIdDoctorIdAndIdSpecializationId(doctorId, specializationId1);
        assertThat(exists, is(false));
    }
}
