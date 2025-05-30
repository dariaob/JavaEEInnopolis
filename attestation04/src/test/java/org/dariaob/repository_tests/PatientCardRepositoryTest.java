package org.dariaob.repository_tests;

import jakarta.transaction.Transactional;
import org.dariaob.TestWithContainer;
import org.dariaob.models.PatientCards;
import org.dariaob.models.Patients;
import org.dariaob.repositories.PatientCardRepository;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * The type Patient card repository test.
 */
@DataJpaTest
@ExtendWith(SpringExtension.class)
@TestPropertySource("classpath:application-test.properties")
public class PatientCardRepositoryTest extends TestWithContainer {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PatientCardRepository repository;

    private Long cardId1;
    private Long cardId2;
    private Long patientId;

    /**
     * Sets up.
     */
    @BeforeEach
    public void setUp() {
        // Создаем две карты пациентов
        PatientCards card1 = new PatientCards();
        card1.setSymptoms("Головная боль");
        card1.setDiagnosis("Мигрень");
        card1.setMeds("Ибупрофен");
        card1.setDeleted(false);
        entityManager.persist(card1);
        cardId1 = card1.getId();

        PatientCards card2 = new PatientCards();
        card2.setSymptoms("Кашель");
        card2.setDiagnosis("Бронхит");
        card2.setMeds("Амброксол");
        card2.setDeleted(false);

        entityManager.persist(card2);
        cardId2 = card2.getId();

        // Создаем пациента и связываем с первой картой
        Patients patient = new Patients();
        patient.setName("Иван Иванов");
        patient.setDeleted(false);
        patient.setBirthDate(LocalDate.from(LocalDateTime.of(2000,1,1,0,0)));
        patient.setInsuranceId(123L);
        patient.setPhone("111");
        patient.setPatientCard(card1);
        entityManager.persist(patient);
        patientId = patient.getId();

        entityManager.flush();
    }

    /**
     * Find all active test.
     */
    @Test
    @DisplayName("PatientCards - Repository - Find all active test")
    public void findAllActiveTest() {
        List<PatientCards> cards = repository.findAllActive();
        assertThat(cards, not(empty()));
        assertThat(cards.stream().allMatch(c -> !c.isDeleted()), is(true));
    }

    /**
     * Find active by id test.
     */
    @Test
    @DisplayName("PatientCards - Repository - Find active by id test")
    public void findActiveByIdTest() {
        Optional<PatientCards> opt = repository.findActiveById(cardId1);
        assertThat(opt.isPresent(), is(true));
        assertThat(opt.get().getId(), equalTo(cardId1));

        // Soft‐delete and verify absence
        repository.softDelete(cardId1);
        entityManager.flush();
        Optional<PatientCards> deletedOpt = repository.findActiveById(cardId1);
        assertThat(deletedOpt.isPresent(), is(false));
    }

    /**
     * Soft delete and restore test.
     */
    @Test
    @DisplayName("PatientCards - Repository - Soft delete and restore test")
    @Transactional
    public void softDeleteAndRestoreTest() {
        // soft delete
        repository.softDelete(cardId2);
        entityManager.flush();
        Optional<PatientCards> afterDelete = repository.findActiveById(cardId2);
        assertThat(afterDelete.isPresent(), is(false));

        // restore
        repository.restore(cardId2);
        entityManager.flush();
        Optional<PatientCards> afterRestore = repository.findActiveById(cardId2);
        assertThat(afterRestore.isPresent(), is(true));
    }

    /**
     * Find by patient id test.
     */
    @Test
    @DisplayName("PatientCards - Repository - Find by patientId test")
    public void findByPatientIdTest() {
        Optional<PatientCards> opt = repository.findByPatientId(patientId);
        assertThat(opt.isPresent(), is(true));
        assertThat(opt.get().getId(), equalTo(cardId1));
    }

    /**
     * Find by diagnosis containing test.
     */
    @Test
    @DisplayName("PatientCards - Repository - Find by diagnosis containing test")
    public void findByDiagnosisContainingTest() {
        List<PatientCards> result = repository.findByDiagnosisContainingIgnoreCase("брон");
        System.out.println(result);
        assertThat(result.stream().anyMatch(c -> c.getId().equals(cardId2)), is(true));
    }
}
