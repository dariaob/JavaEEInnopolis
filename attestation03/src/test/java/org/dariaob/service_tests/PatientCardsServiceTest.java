package org.dariaob.service_tests;

import org.dariaob.exceptions.DataNotFoundException;
import org.dariaob.models.PatientCards;
import org.dariaob.models.PatientCardsHistory;
import org.dariaob.repositories.PatientCardRepository;
import org.dariaob.repositories.PatientCardsHistoryRepository;
import org.dariaob.services.PatientCardsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class PatientCardsServiceTest {

    @Mock
    private PatientCardRepository patientCardRepository;

    @Mock
    private PatientCardsHistoryRepository historyRepository;

    @InjectMocks
    private PatientCardsService service;

    @Test
    @DisplayName("PatientCards - Service - Get all active patient cards")
    void patientCardsGetAllActiveTest() {
        PatientCards card = getPatientCard(1L);
        Mockito.when(patientCardRepository.findAllActive()).thenReturn(List.of(card));

        List<PatientCards> result = service.getAllActive();
        assertThat(result, contains(card));
    }

    @Test
    @DisplayName("PatientCards - Service - Get active patient card by ID - exists")
    void patientCardsGetActiveByIdExistsTest() {
        PatientCards card = getPatientCard(1L);
        Mockito.when(patientCardRepository.findActiveById(1L)).thenReturn(Optional.of(card));

        PatientCards result = service.getActiveById(1L);
        assertThat(result, equalTo(card));
    }

    @Test
    @DisplayName("PatientCards - Service - Get active patient card by ID - not found")
    void patientCardsGetActiveByIdNotFoundTest() {
        Mockito.when(patientCardRepository.findActiveById(99L)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> service.getActiveById(99L));
    }

    @Test
    @DisplayName("PatientCards - Service - Get patient card by patient ID - exists")
    void patientCardsGetByPatientIdExistsTest() {
        PatientCards card = getPatientCard(1L);
        Mockito.when(patientCardRepository.findByPatientId(5L)).thenReturn(Optional.of(card));

        PatientCards result = service.getByPatientId(5L);
        assertThat(result, equalTo(card));
    }

    @Test
    @DisplayName("PatientCards - Service - Get patient card by patient ID - not found")
    void patientCardsGetByPatientIdNotFoundTest() {
        Mockito.when(patientCardRepository.findByPatientId(5L)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> service.getByPatientId(5L));
    }

    @Test
    @DisplayName("PatientCards - Service - Soft delete patient card - success")
    void patientCardsSoftDeleteSuccessTest() {
        Mockito.when(patientCardRepository.findActiveById(1L)).thenReturn(Optional.of(getPatientCard(1L)));

        service.softDelete(1L);
        Mockito.verify(patientCardRepository).softDelete(1L);
    }

    @Test
    @DisplayName("PatientCards - Service - Soft delete patient card - not found")
    void patientCardsSoftDeleteNotFoundTest() {
        Mockito.when(patientCardRepository.findActiveById(1L)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> service.softDelete(1L));
    }

    @Test
    @DisplayName("PatientCards - Service - Restore patient card - success")
    void patientCardsRestoreSuccessTest() {
        Mockito.when(patientCardRepository.findActiveById(1L)).thenReturn(Optional.of(getPatientCard(1L)));

        service.restore(1L);
        Mockito.verify(patientCardRepository).restore(1L);
    }

    @Test
    @DisplayName("PatientCards - Service - Restore patient card - not found")
    void patientCardsRestoreNotFoundTest() {
        Mockito.when(patientCardRepository.findActiveById(1L)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> service.restore(1L));
    }

    @Test
    @DisplayName("PatientCards - Service - Find patient cards by diagnosis")
    void patientCardsFindByDiagnosisTest() {
        PatientCards card = getPatientCard(1L);
        Mockito.when(patientCardRepository.findByDiagnosisContainingIgnoreCase("грипп")).thenReturn(List.of(card));

        List<PatientCards> result = service.findByDiagnosis("грипп");
        assertThat(result, contains(card));
    }

    @Test
    @DisplayName("PatientCards - Service - Get history by patient card ID")
    void patientCardsGetHistoryByCardIdTest() {
        PatientCardsHistory history = new PatientCardsHistory();
        Mockito.when(historyRepository.findByCardId(1L)).thenReturn(List.of(history));

        List<PatientCardsHistory> result = service.getHistoryByCardId(1L);
        assertThat(result, contains(history));
    }

    @Test
    @DisplayName("PatientCards - Service - Get last change by patient card ID - exists")
    void patientCardsGetLastChangeByCardIdExistsTest() {
        PatientCardsHistory history = new PatientCardsHistory();
        Mockito.when(historyRepository.findTopByCardIdOrderByChangedAtDesc(1L)).thenReturn(Optional.of(history));

        PatientCardsHistory result = service.getLastChangeByCardId(1L);
        assertThat(result, equalTo(history));
    }

    @Test
    @DisplayName("PatientCards - Service - Get last change by patient card ID - not found")
    void patientCardsGetLastChangeByCardIdNotFoundTest() {
        Mockito.when(historyRepository.findTopByCardIdOrderByChangedAtDesc(1L)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> service.getLastChangeByCardId(1L));
    }

    @Test
    @DisplayName("PatientCards - Service - Save patient card")
    void patientCardsSaveTest() {
        PatientCards card = getPatientCard(null);
        PatientCards saved = getPatientCard(1L);
        Mockito.when(patientCardRepository.save(card)).thenReturn(saved);

        PatientCards result = service.save(card);
        assertThat(result, equalTo(saved));
    }

    private PatientCards getPatientCard(Long id) {
        return new PatientCards(
                id,
                "Кашель, температура",
                "ОРВИ",
                "Парацетамол",
                false,
                null
        );
    }
}
