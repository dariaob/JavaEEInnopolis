package org.dariaob.service_tests;

import org.dariaob.exceptions.DataNotFoundException;
import org.dariaob.models.Doctors;
import org.dariaob.repositories.DoctorsRepository;
import org.dariaob.services.DoctorsService;
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

@ExtendWith(MockitoExtension.class)
public class DoctorsServiceTest {

    @Mock
    private DoctorsRepository doctorsRepository;

    @InjectMocks
    private DoctorsService doctorsService;

    @Test
    @DisplayName("Doctors - Service - Get all active doctors")
    void getAllActive_ShouldReturnListOfDoctors() {
        // Arrange
        Doctors doctor1 = createTestDoctor(1L, "Dr. Smith", "1234567890");
        Doctors doctor2 = createTestDoctor(2L, "Dr. Johnson", "0987654321");
        when(doctorsRepository.findAllActive()).thenReturn(List.of(doctor1, doctor2));

        // Act
        List<Doctors> result = doctorsService.getAllActive();

        // Assert
        assertThat(result, hasSize(2));
        assertThat(result.get(0).getName(), equalTo("Dr. Smith"));
        assertThat(result.get(1).getPhone(), equalTo("0987654321"));
        verify(doctorsRepository, times(1)).findAllActive();
    }

    @Test
    @DisplayName("Doctors - Service - Get active by ID - Success")
    void getActiveById_WhenDoctorExists_ShouldReturnDoctor() {
        // Arrange
        Doctors doctor = createTestDoctor(1L, "Dr. Smith", "1234567890");
        when(doctorsRepository.findActiveById(1L)).thenReturn(Optional.of(doctor));

        // Act
        Doctors result = doctorsService.getActiveById(1L);

        // Assert
        assertThat(result.getId(), equalTo(1L));
        assertThat(result.getName(), equalTo("Dr. Smith"));
        verify(doctorsRepository, times(1)).findActiveById(1L);
    }

    @Test
    @DisplayName("Doctors - Service - Get active by ID - Not found")
    void getActiveById_WhenDoctorNotExists_ShouldThrowException() {
        // Arrange
        when(doctorsRepository.findActiveById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        DataNotFoundException exception = assertThrows(DataNotFoundException.class,
                () -> doctorsService.getActiveById(1L));

        assertThat(exception.getMessage(), containsString("Врач с ID 1 не найден или удалён"));
        verify(doctorsRepository, times(1)).findActiveById(1L);
    }

    @Test
    @DisplayName("Doctors - Service - Get active by phone - Success")
    void getActiveByPhone_WhenDoctorExists_ShouldReturnDoctor() {
        // Arrange
        Doctors doctor = createTestDoctor(1L, "Dr. Smith", "1234567890");
        when(doctorsRepository.findActiveByPhone("1234567890")).thenReturn(Optional.of(doctor));

        // Act
        Doctors result = doctorsService.getActiveByPhone("1234567890");

        // Assert
        assertThat(result.getId(), equalTo(1L));
        assertThat(result.getPhone(), equalTo("1234567890"));
        verify(doctorsRepository, times(1)).findActiveByPhone("1234567890");
    }

    @Test
    @DisplayName("Doctors - Service - Get active by phone - Not found")
    void getActiveByPhone_WhenDoctorNotExists_ShouldThrowException() {
        // Arrange
        when(doctorsRepository.findActiveByPhone("1234567890")).thenReturn(Optional.empty());

        // Act & Assert
        DataNotFoundException exception = assertThrows(DataNotFoundException.class,
                () -> doctorsService.getActiveByPhone("1234567890"));

        assertThat(exception.getMessage(), containsString("Врач с телефоном 1234567890 не найден или удалён"));
        verify(doctorsRepository, times(1)).findActiveByPhone("1234567890");
    }

    @Test
    @DisplayName("Doctors - Service - Soft delete - Success")
    void softDelete_WhenDoctorExists_ShouldCallRepository() {
        // Arrange
        when(doctorsRepository.findActiveById(1L)).thenReturn(Optional.of(createTestDoctor(1L, "Dr. Smith", "1234567890")));
        doNothing().when(doctorsRepository).softDelete(1L);

        // Act
        doctorsService.softDelete(1L);

        // Assert
        verify(doctorsRepository, times(1)).findActiveById(1L);
        verify(doctorsRepository, times(1)).softDelete(1L);
    }

    @Test
    @DisplayName("Doctors - Service - Soft delete - Not found")
    void softDelete_WhenDoctorNotExists_ShouldThrowException() {
        // Arrange
        when(doctorsRepository.findActiveById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        DataNotFoundException exception = assertThrows(DataNotFoundException.class,
                () -> doctorsService.softDelete(1L));

        assertThat(exception.getMessage(), containsString("Врач с ID 1 не найден или уже удалён"));
        verify(doctorsRepository, times(1)).findActiveById(1L);
        verify(doctorsRepository, never()).softDelete(any());
    }

    @Test
    @DisplayName("Doctors - Service - Restore - Success")
    void restore_WhenDoctorExists_ShouldCallRepository() {
        // Arrange
        when(doctorsRepository.findActiveById(1L)).thenReturn(Optional.of(createTestDoctor(1L, "Dr. Smith", "1234567890")));
        doNothing().when(doctorsRepository).restore(1L);

        // Act
        doctorsService.restore(1L);

        // Assert
        verify(doctorsRepository, times(1)).findActiveById(1L);
        verify(doctorsRepository, times(1)).restore(1L);
    }

    @Test
    @DisplayName("Doctors - Service - Restore - Not found")
    void restore_WhenDoctorNotExists_ShouldThrowException() {
        // Arrange
        when(doctorsRepository.findActiveById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        DataNotFoundException exception = assertThrows(DataNotFoundException.class,
                () -> doctorsService.restore(1L));

        assertThat(exception.getMessage(), containsString("Врач с ID 1 не найден или уже восстановлен"));
        verify(doctorsRepository, times(1)).findActiveById(1L);
        verify(doctorsRepository, never()).restore(any());
    }

    @Test
    @DisplayName("Doctors - Service - Save doctor")
    void save_ShouldReturnSavedDoctor() {
        // Arrange
        Doctors doctorToSave = createTestDoctor(null, "New Doctor", "9876543210");
        Doctors savedDoctor = createTestDoctor(1L, "New Doctor", "9876543210");
        when(doctorsRepository.save(doctorToSave)).thenReturn(savedDoctor);

        // Act
        Doctors result = doctorsService.save(doctorToSave);

        // Assert
        assertThat(result.getId(), equalTo(1L));
        assertThat(result.getName(), equalTo("New Doctor"));
        verify(doctorsRepository, times(1)).save(doctorToSave);
    }

    private Doctors createTestDoctor(Long id, String name, String phone) {
        Doctors doctor = new Doctors();
        doctor.setId(id);
        doctor.setName(name);
        doctor.setPhone(phone);
        doctor.setDeleted(false);
        return doctor;
    }
}
