package org.dariaob.controller_tests;

import org.dariaob.TestWithContainer;
import org.dariaob.controllers.OfficesController;
import org.dariaob.dto.offices.OfficeDto;
import org.dariaob.exceptions.DataNotFoundException;
import org.dariaob.models.Offices;
import org.dariaob.repositories.OfficesRepository;
import org.dariaob.services.OfficesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class OfficesControllerTest extends TestWithContainer {

    @Autowired
    private OfficesController officesController;

    @MockBean
    private OfficesService officesService;

    @Autowired
    private OfficesRepository officesRepository;

    @BeforeEach
    void setUp() {
        officesRepository.softDeleteAll();
    }

    @Test
    @DisplayName("Offices - Controller - Get all active offices - Success")
    void getAllActiveOfficesTest() {
        Offices office1 = createAndSaveOffice("Office 1");
        Offices office2 = createAndSaveOffice("Office 2");

        when(officesService.getAllActiveOffices()).thenReturn(List.of(office1, office2));

        List<OfficeDto> result = officesController.getAllActiveOffices();

        assertEquals(2, result.size());
        assertEquals("Office 1", result.get(0).getName());
        assertEquals("Office 2", result.get(1).getName());
    }

    @Test
    @DisplayName("Offices - Controller - Get office by id - Found")
    void getOfficeByIdTest() {
        Offices office = createAndSaveOffice("Test Office");
        when(officesService.getActiveOfficeById(office.getId())).thenReturn(office);

        OfficeDto result = officesController.getOfficeById(office.getId());

        assertNotNull(result);
        assertEquals(office.getId(), result.getId());
        assertEquals("Test Office", result.getName());
    }

    @Test
    @DisplayName("Offices - Controller - Get office by id - Not found")
    void getOfficeByIdNotFoundTest() {
        Long nonExistentId = 999L;
        when(officesService.getActiveOfficeById(nonExistentId))
                .thenThrow(new DataNotFoundException("Office not found"));

        assertThrows(DataNotFoundException.class, () ->
                officesController.getOfficeById(nonExistentId));
    }

    @Test
    @DisplayName("Offices - Controller - Create new office - Success")
    void createOfficeTest() {
        Offices office = new Offices();
        office.setName("New Office");
        OfficeDto requestDto = new OfficeDto(office);

        when(officesService.saveOffice(any(Offices.class)))
                .thenAnswer(inv -> {
                    Offices o = inv.getArgument(0);
                    o.setId(1L);
                    return o;
                });

        OfficeDto result = officesController.createOffice(requestDto);

        assertNotNull(result.getId());
        assertEquals("New Office", result.getName());
    }

    @Test
    @DisplayName("Offices - Controller - Soft delete office - Success")
    void softDeleteOfficeTest() {
        Offices office = createAndSaveOffice("To be deleted");
        doNothing().when(officesService).softDeleteOffice(office.getId());

        officesController.softDeleteOffice(office.getId());

        verify(officesService).softDeleteOffice(office.getId());
    }

    @Test
    @DisplayName("Offices - Controller - Restore office - Success")
    void restoreOfficeTest() {
        Offices office = createAndSaveOffice("To be restored");
        doNothing().when(officesService).restoreOffice(office.getId());

        officesController.restoreOffice(office.getId());

        verify(officesService).restoreOffice(office.getId());
    }

    private Offices createAndSaveOffice(String name) {
        Offices office = new Offices();
        office.setName(name);
        return officesRepository.save(office);
    }
}
