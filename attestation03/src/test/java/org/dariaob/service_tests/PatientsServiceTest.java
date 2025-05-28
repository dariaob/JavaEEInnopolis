package org.dariaob.service_tests;

import org.dariaob.exceptions.DataNotFoundException;
import org.dariaob.models.PatientCards;
import org.dariaob.models.Patients;
import org.dariaob.repositories.PatientsRepository;
import org.dariaob.services.PatientsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class PatientsServiceTest {

    @Mock
    private PatientsRepository patientsRepository;

    @InjectMocks
    private PatientsService patientsService;

    @Test
    @DisplayName("Patients - Service - Получение всех активных пациентов")
    void patientsGetAllActiveTest() {
        Patients patient = buildPatient(1L);
        Mockito.when(patientsRepository.findAllActive()).thenReturn(List.of(patient));

        List<Patients> result = patientsService.getAllActive();
        assertThat(result, hasSize(1));
        assertThat(result.get(0).getId(), is(1L));
    }

    @Test
    @DisplayName("Patients - Service - Получение активного пациента по ID (успех)")
    void patientsGetActiveByIdFoundTest() {
        Patients patient = buildPatient(1L);
        Mockito.when(patientsRepository.findActiveById(1L)).thenReturn(Optional.of(patient));

        Patients result = patientsService.getActiveById(1L);
        assertThat(result, equalTo(patient));
    }

    @Test
    @DisplayName("Patients - Service - Получение активного пациента по ID (не найден)")
    void patientsGetActiveByIdNotFoundTest() {
        Mockito.when(patientsRepository.findActiveById(2L)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> patientsService.getActiveById(2L));
    }

    @Test
    @DisplayName("Patients - Service - Сохранение пациента")
    void patientsSaveTest() {
        Patients newPatient = buildPatient(null);
        Patients savedPatient = buildPatient(10L);

        Mockito.when(patientsRepository.save(newPatient)).thenReturn(savedPatient);

        Patients result = patientsService.save(newPatient);
        assertThat(result.getId(), is(10L));
        assertThat(result.getName(), is("Иван Иванов"));
    }

    @Test
    @DisplayName("Patients - Service - Мягкое удаление пациента (успех)")
    void patientsSoftDeleteSuccessTest() {
        Patients patient = buildPatient(1L);
        Mockito.when(patientsRepository.findActiveById(1L)).thenReturn(Optional.of(patient));

        patientsService.softDelete(1L);

        Mockito.verify(patientsRepository).softDelete(1L);
    }

    @Test
    @DisplayName("Patients - Service - Мягкое удаление пациента (не найден)")
    void patientsSoftDeleteNotFoundTest() {
        Mockito.when(patientsRepository.findActiveById(999L)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> patientsService.softDelete(999L));
    }

    @Test
    @DisplayName("Patients - Service - Восстановление пациента (успех)")
    void patientsRestoreSuccessTest() {
        Patients patient = buildPatient(1L);
        Mockito.when(patientsRepository.findActiveById(1L)).thenReturn(Optional.of(patient));

        patientsService.restore(1L);

        Mockito.verify(patientsRepository).restore(1L);
    }

    @Test
    @DisplayName("Patients - Service - Восстановление пациента (не найден)")
    void patientsRestoreNotFoundTest() {
        Mockito.when(patientsRepository.findActiveById(1L)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> patientsService.restore(1L));
    }

    @Test
    @DisplayName("Patients - Service - Получение пациента по номеру телефона (успех)")
    void patientsFindByPhoneSuccessTest() {
        Patients patient = buildPatient(1L);
        Mockito.when(patientsRepository.findByPhone("+79991234567")).thenReturn(Optional.of(patient));

        Patients result = patientsService.getActiveByPhone("+79991234567");
        assertThat(result.getId(), is(1L));
    }

    @Test
    @DisplayName("Patients - Service - Получение пациента по номеру телефона (не найден)")
    void patientsFindByPhoneNotFoundTest() {
        Mockito.when(patientsRepository.findByPhone("+70000000000")).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> patientsService.getActiveByPhone("+70000000000"));
    }

    // Вспомогательный метод
    private Patients buildPatient(Long id) {
        PatientCards card = new PatientCards();
        card.setId(100L);
        card.setSymptoms("Симптомы");
        card.setDiagnosis("Диагноз");
        card.setMeds("Лекарства");
        card.setDeleted(false);

        Patients patient = new Patients();
        patient.setId(id);
        patient.setName("Иван Иванов");
        patient.setBirthDate(LocalDate.of(1990, 1, 1));
        patient.setPhone("+79991234567");
        patient.setInsuranceId(123456L);
        patient.setDeleted(false);
        patient.setPatientCard(card);

        return patient;
    }
}
