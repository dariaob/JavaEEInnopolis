package org.dariaob.repository_tests;

import org.dariaob.TestWithContainer;
import org.dariaob.models.PatientCards;
import org.dariaob.models.PatientCardsHistory;
import org.dariaob.repositories.PatientCardsHistoryRepository;
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
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@TestPropertySource("classpath:application-test.properties")
public class PatientCardsHistoryRepositoryTest extends TestWithContainer {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PatientCardsHistoryRepository repository;

    private Long cardId;

    @BeforeEach
    public void setUp() {
        PatientCards card = new PatientCards();
        card.setDiagnosis("ОРВИ");
        card.setMeds("Парацетамол");
        card.setSymptoms("Температура");
        card.setDeleted(false);
        entityManager.persist(card);
        cardId = card.getId();

        PatientCardsHistory h1 = new PatientCardsHistory();
        h1.setCard(card);
        h1.setChangedBy("admin");
        h1.setChangedAt(LocalDateTime.now().minusHours(2));
        h1.setOldDiagnosis("ОРВИ");
        h1.setNewDiagnosis("Грипп");
        h1.setOldMeds("Парацетамол");
        h1.setNewMeds("Ибупрофен");
        h1.setChangeReason("Обновление симптомов");
        entityManager.persist(h1);

        PatientCardsHistory h2 = new PatientCardsHistory();
        h2.setCard(card);
        h2.setChangedBy("admin");
        h2.setChangedAt(LocalDateTime.now().minusHours(1));
        h2.setOldDiagnosis("Грипп");
        h2.setNewDiagnosis("Пневмония");
        h2.setOldMeds("Ибупрофен");
        h2.setNewMeds("Азитромицин");
        h2.setChangeReason("Уточнение диагноза");
        entityManager.persist(h2);

        entityManager.flush();
    }

    @Test
    @DisplayName("PatientCardsHistory - Repository - Find by cardId")
    public void findByCardIdTest() {
        List<PatientCardsHistory> list = repository.findByCardId(cardId);
        assertThat(list, not(empty()));
        assertThat(list.size(), is(2));
    }

    @Test
    @DisplayName("PatientCardsHistory - Repository - Find last change by cardId")
    public void findLastChangeByCardIdTest() {
        Optional<PatientCardsHistory> opt = repository.findTopByCardIdOrderByChangedAtDesc(cardId);
        assertThat(opt.isPresent(), is(true));
        assertThat(opt.get().getNewDiagnosis(), equalTo("Пневмония"));
        assertThat(opt.get().getNewMeds(), equalTo("Азитромицин"));
    }

    @Test
    @DisplayName("PatientCardsHistory - Repository - Find by changedBy")
    public void findByChangedByTest() {
        List<PatientCardsHistory> list = repository.findByChangedBy("admin");
        assertThat(list, not(empty()));
        assertThat(list.stream().allMatch(h -> h.getChangedBy().equals("admin")), is(true));
    }

    @Test
    @DisplayName("PatientCardsHistory - Repository - Return empty for unknown cardId")
    public void findByUnknownCardIdTest() {
        List<PatientCardsHistory> result = repository.findByCardId(-999L);
        assertThat(result, is(empty()));
    }

    @Test
    @DisplayName("PatientCardsHistory - Repository - Return empty for unknown changedBy")
    public void findByUnknownChangedByTest() {
        List<PatientCardsHistory> result = repository.findByChangedBy("ghost");
        assertThat(result, is(empty()));
    }
}
