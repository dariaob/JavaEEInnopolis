package org.dariaob.service_tests;

import org.dariaob.exceptions.DataNotFoundException;
import org.dariaob.models.DoctorSpecializationId;
import org.dariaob.models.DoctorSpecializations;
import org.dariaob.repositories.DoctorSpecializationsRepository;
import org.dariaob.services.DoctorSpecializationsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * The type Doctor specializations service test.
 */
@ExtendWith(MockitoExtension.class)
public class DoctorSpecializationsServiceTest {

    @Mock
    private DoctorSpecializationsRepository repository;

    @InjectMocks
    private DoctorSpecializationsService service;

    /**
     * Gets specializations by doctor id test.
     */
    @Test
    @DisplayName("DoctorSpecializations - Service - Get specializations by doctorId")
    public void getSpecializationsByDoctorIdTest() {
        DoctorSpecializations ds1 = getTestLink(1L, 10L);
        DoctorSpecializations ds2 = getTestLink(1L, 11L);

        Mockito.when(repository.findAllByIdDoctorId(1L)).thenReturn(List.of(ds1, ds2));

        List<DoctorSpecializations> result = service.getSpecializationsByDoctorId(1L);

        assertThat(result, hasSize(2));
        assertThat(result.get(0).getId().getDoctorId(), equalTo(1L));
        assertThat(result.get(1).getId().getSpecializationId(), equalTo(11L));
    }

    /**
     * Save link test.
     */
    @Test
    @DisplayName("DoctorSpecializations - Service - Save link")
    public void saveLinkTest() {
        DoctorSpecializations link = getTestLink(2L, 20L);

        Mockito.when(repository.save(link)).thenReturn(link);

        DoctorSpecializations result = service.save(link);

        assertThat(result.getId().getDoctorId(), equalTo(2L));
        assertThat(result.getId().getSpecializationId(), equalTo(20L));
    }

    /**
     * Exists test.
     */
    @Test
    @DisplayName("DoctorSpecializations - Service - Exists test")
    public void existsTest() {
        Mockito.when(repository.existsByIdDoctorIdAndIdSpecializationId(3L, 30L)).thenReturn(true);

        boolean exists = service.existsByDoctorAndSpecialization(3L, 30L);
        assertThat(exists, is(true));
    }

    /**
     * Delete existing test.
     */
    @Test
    @DisplayName("DoctorSpecializations - Service - Delete test - Exists")
    public void deleteExistingTest() {
        Mockito.when(repository.existsByIdDoctorIdAndIdSpecializationId(4L, 40L)).thenReturn(true);

        service.deleteSpecialization(4L, 40L);

        Mockito.verify(repository).deleteByDoctorIdAndSpecializationId(4L, 40L);
    }

    /**
     * Delete non existing test.
     */
    @Test
    @DisplayName("DoctorSpecializations - Service - Delete test - Not exists")
    public void deleteNonExistingTest() {
        Mockito.when(repository.existsByIdDoctorIdAndIdSpecializationId(5L, 50L)).thenReturn(false);

        DataNotFoundException exception = assertThrows(
                DataNotFoundException.class,
                () -> service.deleteSpecialization(5L, 50L)
        );

        assertThat(exception.getMessage(), equalTo("Связь врача с специализацией не найдена."));
    }

    private DoctorSpecializations getTestLink(Long doctorId, Long specializationId) {
        DoctorSpecializations link = new DoctorSpecializations();
        link.setId(new DoctorSpecializationId(doctorId, specializationId));
        return link;
    }
}
