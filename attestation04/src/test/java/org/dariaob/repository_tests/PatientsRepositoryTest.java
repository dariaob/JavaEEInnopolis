package org.dariaob.repository_tests;

import jakarta.transaction.Transactional;
import org.dariaob.TestWithContainer;
import org.dariaob.models.PatientCards;
import org.dariaob.models.Patients;
import org.dariaob.repositories.PatientsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * The type Patients repository test.
 */
@DataJpaTest
@ExtendWith(SpringExtension.class)
@TestPropertySource("classpath:application-test.properties")
public class PatientsRepositoryTest extends TestWithContainer {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PatientsRepository repository;

    private Long activePatientId1;
    private Long activePatientId2;
    private Long deletedPatientId;
    private String activePhone;

    /**
     * Sets up.
     */
    @BeforeEach
    public void setUp() {
        PatientCards card1 = createCard("Температура", "ОРВИ", "Парацетамол", false);
        PatientCards card2 = createCard("Головная боль", "Мигрень", null, false);
        PatientCards card3 = createCard("Травма", "Перелом", "Обезболивающее", true);

        entityManager.persist(card1);
        entityManager.persist(card2);
        entityManager.persist(card3);

        Patients p1 = createPatient("Иванов Иван", card1, "111", false, 123L);
        Patients p2 = createPatient("Петров Петр", card2, "222", false, 456L);
        Patients p3 = createPatient("Сидоров Сидор", card3, "333", true, 789L);

        entityManager.persist(p1);
        entityManager.persist(p2);
        entityManager.persist(p3);
        entityManager.flush();

        this.activePatientId1 = p1.getId();
        this.activePatientId2 = p2.getId();
        this.deletedPatientId = p3.getId();
        this.activePhone = p2.getPhone();
    }

    private PatientCards createCard(String symptoms, String diagnosis, String meds, boolean isDeleted) {
        PatientCards card = new PatientCards();
        card.setSymptoms(symptoms);
        card.setDiagnosis(diagnosis);
        card.setMeds(meds);
        card.setDeleted(isDeleted);
        return card;
    }

    private Patients createPatient(String name, PatientCards card, String phone, boolean isDeleted, Long insId) {
        Patients p = new Patients();
        p.setName(name);
        p.setBirthDate(LocalDate.of(1990, 1, 1));
        p.setPhone(phone);
        p.setDeleted(isDeleted);
        p.setInsuranceId(insId);
        p.setPatientCard(card);
        return p;
    }

    /**
     * Find all active test.
     */
    @Test
    @DisplayName("Patients - Repository - Find all active test")
    public void findAllActiveTest() {
        List<Patients> patients = repository.findAllActive();
        assertThat(patients.stream().allMatch(p -> !p.isDeleted()), is(true));
    }

    /**
     * Find active by id test.
     */
    @Test
    @DisplayName("Patients - Repository - Find active by ID test")
    public void findActiveByIdTest() {
        Optional<Patients> patient = repository.findActiveById(activePatientId1);
        assertThat(patient.isPresent(), is(true));
        assertThat(patient.get().isDeleted(), is(false));

        Optional<Patients> deleted = repository.findActiveById(deletedPatientId);
        assertThat(deleted.isPresent(), is(false));
    }

    /**
     * Find by phone test.
     */
    @Test
    @DisplayName("Patients - Repository - Find by phone test")
    public void findByPhoneTest() {
        Optional<Patients> patient = repository.findByPhone(activePhone);
        assertThat(patient.isPresent(), is(true));
        assertThat(patient.get().getPhone(), is(activePhone));
    }

    /**
     * Soft delete test.
     */
    @Test
    @DisplayName("Patients - Repository - Soft delete test")
    @Transactional
    public void softDeleteTest() {
        repository.softDelete(activePatientId1);
        entityManager.flush();
        entityManager.clear();

        Patients deleted = entityManager.find(Patients.class, activePatientId1);
        assertThat(deleted.isDeleted(), is(true));
    }

    /**
     * Restore test.
     */
    @Test
    @DisplayName("Patients - Repository - Restore test")
    @Transactional
    public void restoreTest() {
        repository.softDelete(activePatientId2);
        repository.restore(activePatientId2);
        entityManager.flush();
        entityManager.clear();

        Patients restored = entityManager.find(Patients.class, activePatientId2);
        assertThat(restored.isDeleted(), is(false));
    }

    /**
     * Find all includes deleted.
     */
    @Test
    @DisplayName("Patients - Repository - Standard findAll includes deleted")
    public void findAllIncludesDeleted() {
        List<Patients> allPatients = repository.findAll();
        // Проверяем что есть все наши кабинеты
        List<Long> ids = allPatients.stream().map(Patients::getId).toList();
        assertThat(ids, hasItems(activePatientId1, activePatientId2, deletedPatientId));

        // Проверяем флаги isDeleted
        Map<Long, Boolean> officeDeletionStatus = allPatients.stream()
                .collect(Collectors.toMap(Patients::getId, Patients::isDeleted));

        assertThat(officeDeletionStatus.get(activePatientId1), is(false));
        assertThat(officeDeletionStatus.get(activePatientId2), is(false));
        assertThat(officeDeletionStatus.get(deletedPatientId), is(true));
    }
}
