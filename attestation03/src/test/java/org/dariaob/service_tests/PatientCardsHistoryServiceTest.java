package org.dariaob.service_tests;

import org.dariaob.models.PatientCards;
import org.dariaob.models.PatientCardsHistory;
import org.dariaob.repositories.PatientCardsHistoryRepository;
import org.dariaob.services.PatientCardsHistoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@ExtendWith(MockitoExtension.class)
public class PatientCardsHistoryServiceTest {

    @Mock
    private PatientCardsHistoryRepository repository;

    @InjectMocks
    private PatientCardsHistoryService service;

    @Test
    @DisplayName("PatientCardsHistory - Service - Get all by cardId")
    public void getByCardIdTest() {
        PatientCards card = getTestCard(1L);
        PatientCardsHistory h1 = getTestHistory(1L, card);
        PatientCardsHistory h2 = getTestHistory(2L, card);

        Mockito.when(repository.findByCardId(1L)).thenReturn(List.of(h1, h2));

        List<PatientCardsHistory> result = service.getByCardId(1L);
        assertThat(result, hasSize(2));
        assertThat(result.get(0).getId(), equalTo(1L));
        assertThat(result.get(1).getId(), equalTo(2L));
    }

    @Test
    @DisplayName("PatientCardsHistory - Service - Get last change by cardId")
    public void getLastChangeByCardIdTest() {
        PatientCards card = getTestCard(1L);
        PatientCardsHistory last = getTestHistory(3L, card);

        Mockito.when(repository.findTopByCardIdOrderByChangedAtDesc(1L)).thenReturn(Optional.of(last));

        Optional<PatientCardsHistory> result = service.getLastChangeByCardId(1L);

        assertThat(result.isPresent(), is(true));
        assertThat(result.get().getId(), equalTo(3L));
    }

    @Test
    @DisplayName("PatientCardsHistory - Service - Get all changes by username")
    public void getByChangedByTest() {
        PatientCardsHistory h1 = getTestHistory(1L, getTestCard(1L));
        PatientCardsHistory h2 = getTestHistory(2L, getTestCard(2L));

        Mockito.when(repository.findByChangedBy("doctor")).thenReturn(List.of(h1, h2));

        List<PatientCardsHistory> result = service.getByChangedBy("doctor");
        assertThat(result, hasSize(2));
        assertThat(result.get(0).getChangedBy(), equalTo("doctor"));
    }

    @Test
    @DisplayName("PatientCardsHistory - Service - Create new history entry")
    public void createTest() {
        PatientCardsHistory entry = getTestHistory(1L, getTestCard(1L));

        Mockito.when(repository.save(entry)).thenReturn(entry);

        PatientCardsHistory result = service.create(entry);
        assertThat(result.getId(), equalTo(1L));
        assertThat(result.getNewDiagnosis(), equalTo("New diagnosis"));
    }

    private PatientCards getTestCard(Long id) {
        PatientCards card = new PatientCards();
        card.setId(id);
        return card;
    }

    private PatientCardsHistory getTestHistory(Long id, PatientCards card) {
        PatientCardsHistory history = new PatientCardsHistory();
        history.setId(id);
        history.setCard(card);
        history.setChangedAt(LocalDateTime.now());
        history.setChangedBy("doctor");
        history.setOldDiagnosis("Old diagnosis");
        history.setNewDiagnosis("New diagnosis");
        history.setOldMeds("Old meds");
        history.setNewMeds("New meds");
        history.setChangeReason("Correction");
        return history;
    }
}
