package org.dariaob.service_tests;

import org.dariaob.exceptions.DataNotFoundException;
import org.dariaob.models.Offices;
import org.dariaob.repositories.OfficesRepository;
import org.dariaob.services.OfficesService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * The type Offices service test.
 */
@ExtendWith(MockitoExtension.class)
public class OfficesServiceTest {

    @Mock
    private OfficesRepository officesRepository;

    @InjectMocks
    private OfficesService officesService;

    /**
     * Gets all active offices test.
     */
    @Test
    @DisplayName("Offices - Service - Get all active offices")
    public void getAllActiveOfficesTest() {
        Offices office1 = createTestOffice(1L, "A101");
        Offices office2 = createTestOffice(2L, "B202");
        when(officesRepository.findAllActive()).thenReturn(List.of(office1, office2));

        List<Offices> result = officesService.getAllActiveOffices();

        assertThat(result, hasSize(2));
        assertThat(result.get(0).getName(), equalTo("A101"));
        verify(officesRepository).findAllActive();
    }

    /**
     * Gets active office by id test.
     */
    @Test
    @DisplayName("Offices - Service - Get active office by ID - Success")
    public void getActiveOfficeByIdTest() {
        Offices office = createTestOffice(1L, "A101");
        when(officesRepository.findActiveById(1L)).thenReturn(Optional.of(office));

        Offices result = officesService.getActiveOfficeById(1L);

        assertThat(result.getId(), equalTo(1L));
        assertThat(result.getName(), equalTo("A101"));
        verify(officesRepository).findActiveById(1L);
    }

    /**
     * Gets active office by id not found test.
     */
    @Test
    @DisplayName("Offices - Service - Get active office by ID - Not found")
    public void getActiveOfficeByIdNotFoundTest() {
        when(officesRepository.findActiveById(1L)).thenReturn(Optional.empty());

        DataNotFoundException exception = assertThrows(DataNotFoundException.class,
                () -> officesService.getActiveOfficeById(1L));

        assertThat(exception.getMessage(), containsString("Офис с ID 1 не найден"));
        verify(officesRepository).findActiveById(1L);
    }

    /**
     * Soft delete office test.
     */
    @Test
    @DisplayName("Offices - Service - Soft delete office - Success")
    public void softDeleteOfficeTest() {
        when(officesRepository.findActiveById(1L)).thenReturn(Optional.of(createTestOffice(1L, "A101")));

        officesService.softDeleteOffice(1L);

        verify(officesRepository).softDelete(1L);
    }

    /**
     * Soft delete office not found test.
     */
    @Test
    @DisplayName("Offices - Service - Soft delete office - Not found")
    public void softDeleteOfficeNotFoundTest() {
        when(officesRepository.findActiveById(1L)).thenReturn(Optional.empty());

        DataNotFoundException exception = assertThrows(DataNotFoundException.class,
                () -> officesService.softDeleteOffice(1L));

        assertThat(exception.getMessage(), containsString("Нельзя удалить: офис с ID 1 не найден"));
        verify(officesRepository).findActiveById(1L);
        verify(officesRepository, never()).softDelete(any());
    }

    /**
     * Restore office test.
     */
    @Test
    @DisplayName("Offices - Service - Restore office")
    public void restoreOfficeTest() {
        officesService.restoreOffice(1L);

        verify(officesRepository).restore(1L);
    }

    /**
     * Save office test.
     */
    @Test
    @DisplayName("Offices - Service - Save office")
    public void saveOfficeTest() {
        Offices officeToSave = createTestOffice(null, "New Office");
        Offices savedOffice = createTestOffice(1L, "New Office");

        when(officesRepository.save(officeToSave)).thenReturn(savedOffice);

        Offices result = officesService.saveOffice(officeToSave);

        assertThat(result.getId(), equalTo(1L));
        assertThat(result.getName(), equalTo("New Office"));
        verify(officesRepository).save(officeToSave);
    }

    private Offices createTestOffice(Long id, String name) {
        Offices office = new Offices();
        office.setId(id);
        office.setName(name);
        office.setDeleted(false);
        return office;
    }
}
