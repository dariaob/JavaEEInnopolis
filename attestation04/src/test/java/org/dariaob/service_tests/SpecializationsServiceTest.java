package org.dariaob.service_tests;

import org.dariaob.exceptions.DataNotFoundException;
import org.dariaob.models.Specializations;
import org.dariaob.repositories.SpecializationsRepository;
import org.dariaob.services.SpecializationsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * The type Specializations service test.
 */
@ExtendWith(MockitoExtension.class)
class SpecializationsServiceTest {

    @Mock
    private SpecializationsRepository specializationsRepository;

    @InjectMocks
    private SpecializationsService specializationsService;

    /**
     * Gets all active test.
     */
    @Test
    @DisplayName("Specializations - Service - Get all active")
    void getAllActiveTest() {
        Specializations spec = new Specializations();
        when(specializationsRepository.findAllActive()).thenReturn(List.of(spec));

        List<Specializations> result = specializationsService.getAllActive();

        assertThat(result).containsExactly(spec);
        verify(specializationsRepository).findAllActive();
    }

    /**
     * Gets active by id when exists test.
     */
    @Test
    @DisplayName("Specializations - Service - Get active by ID (exists)")
    void getActiveByIdWhenExistsTest() {
        Specializations spec = new Specializations();
        when(specializationsRepository.findActiveById(1L)).thenReturn(Optional.of(spec));

        Specializations result = specializationsService.getActiveById(1L);

        assertThat(result).isEqualTo(spec);
        verify(specializationsRepository).findActiveById(1L);
    }

    /**
     * Gets active by id when not found test.
     */
    @Test
    @DisplayName("Specializations - Service - Get active by ID (not found)")
    void getActiveByIdWhenNotFoundTest() {
        when(specializationsRepository.findActiveById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specializationsService.getActiveById(1L))
                .isInstanceOf(DataNotFoundException.class)
                .hasMessageContaining("не найдена");
    }

    /**
     * Gets by name ignore case when exists test.
     */
    @Test
    @DisplayName("Specializations - Service - Get by name ignore case (exists)")
    void getByNameIgnoreCaseWhenExistsTest() {
        Specializations spec = new Specializations();
        when(specializationsRepository.findFirstByNameIgnoreCaseAndIsDeletedFalseOrderByIdDesc("терапевт"))
                .thenReturn(Optional.of(spec));

        Specializations result = specializationsService.getByNameIgnoreCase("терапевт");

        assertThat(result).isEqualTo(spec);
    }

    /**
     * Gets by name ignore case when not found test.
     */
    @Test
    @DisplayName("Specializations - Service - Get by name ignore case (not found)")
    void getByNameIgnoreCaseWhenNotFoundTest() {
        when(specializationsRepository.findFirstByNameIgnoreCaseAndIsDeletedFalseOrderByIdDesc("хирург"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> specializationsService.getByNameIgnoreCase("хирург"))
                .isInstanceOf(DataNotFoundException.class)
                .hasMessageContaining("не найдена");
    }

    /**
     * Soft delete when exists test.
     */
    @Test
    @DisplayName("Specializations - Service - Soft delete (exists)")
    void softDeleteWhenExistsTest() {
        when(specializationsRepository.findActiveById(2L)).thenReturn(Optional.of(new Specializations()));

        specializationsService.softDelete(2L);

        verify(specializationsRepository).softDelete(2L);
    }

    /**
     * Soft delete when not found test.
     */
    @Test
    @DisplayName("Specializations - Service - Soft delete (not found)")
    void softDeleteWhenNotFoundTest() {
        when(specializationsRepository.findActiveById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specializationsService.softDelete(2L))
                .isInstanceOf(DataNotFoundException.class)
                .hasMessageContaining("не найдена");
    }

    /**
     * Restore when exists test.
     */
    @Test
    @DisplayName("Specializations - Service - Restore (exists)")
    void restoreWhenExistsTest() {
        when(specializationsRepository.findActiveById(3L)).thenReturn(Optional.of(new Specializations()));

        specializationsService.restore(3L);

        verify(specializationsRepository).restore(3L);
    }

    /**
     * Restore when not found test.
     */
    @Test
    @DisplayName("Specializations - Service - Restore (not found)")
    void restoreWhenNotFoundTest() {
        when(specializationsRepository.findActiveById(3L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specializationsService.restore(3L))
                .isInstanceOf(DataNotFoundException.class)
                .hasMessageContaining("не найдена");
    }

    /**
     * Save test.
     */
    @Test
    @DisplayName("Specializations - Service - Save specialization")
    void saveTest() {
        Specializations spec = new Specializations();
        when(specializationsRepository.save(spec)).thenReturn(spec);

        Specializations saved = specializationsService.save(spec);

        assertThat(saved).isEqualTo(spec);
        verify(specializationsRepository).save(spec);
    }

    /**
     * Search by name test.
     */
    @Test
    @DisplayName("Specializations - Service - Search by name part")
    void searchByNameTest() {
        Specializations spec = new Specializations();
        when(specializationsRepository.searchByName("лог")).thenReturn(List.of(spec));

        List<Specializations> result = specializationsService.searchByName("лог");

        assertThat(result).containsExactly(spec);
        verify(specializationsRepository).searchByName("лог");
    }
}
