package org.dariaob.controller_tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.dariaob.Attestation03Application;
import org.dariaob.controllers.AppointmentsController;
import org.dariaob.dto.appointments.AppointmentResponseDto;
import org.dariaob.models.*;
import org.dariaob.repositories.UsersRepository;
import org.dariaob.security.jwt.JwtFilter;
import org.dariaob.security.jwt.JwtService;
import org.dariaob.security.users.UsersDetailsServiceImpl;
import org.dariaob.services.AppointmentsService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import java.time.LocalDateTime;
import java.util.List;
import org.dariaob.TestWithContainer;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {
        AppointmentsController.class,
        JwtService.class,
        UsersDetailsServiceImpl.class,
        JwtFilter.class,
        UsersRepository.class
})
@ContextConfiguration(classes = Attestation03Application.class)
@TestPropertySource(locations = "classpath:application-test.properties")
public class AppointmentsControllerTest extends TestWithContainer {

    @Autowired
    private WebApplicationContext context;

    @MockitoBean
    private AppointmentsService appointmentsService;

    private final ObjectMapper mapper = new ObjectMapper();

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @SneakyThrows
    @DisplayName("Appointments - Controller - Get all active")
    public void getAllActiveAppointmentsTest() {
        List<Appointments> appointments = List.of(getAppointmentForTest());
        Mockito.when(appointmentsService.getAllActiveAppointments()).thenReturn(appointments);

        String expected = mapper.writeValueAsString(
                appointments.stream().map(AppointmentResponseDto::new).toList()
        );

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/appointments/active"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(expected));
    }

    @Test
    @SneakyThrows
    @WithMockUser
    @DisplayName("Appointments - Controller - Get by id")
    public void getActiveAppointmentByIdTest() {
        Appointments appointment = getAppointmentForTest();
        Mockito.when(appointmentsService.getActiveAppointmentById(1L)).thenReturn(appointment);

        String expected = mapper.writeValueAsString(new AppointmentResponseDto(appointment));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/appointments/1"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(expected));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Appointments - Controller - Soft delete")
    public void softDeleteAppointmentTest() {
        Mockito.doNothing().when(appointmentsService).softDeleteAppointment(1L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/appointments/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Appointments - Controller - Restore")
    public void restoreAppointmentTest() {
        Long appointmentId = 1L;
        Mockito.doNothing().when(appointmentsService).restoreAppointment(appointmentId);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/appointments/{id}/restore", appointmentId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        // проверка, что метод сервиса действительно вызвался
        Mockito.verify(appointmentsService, Mockito.times(1)).restoreAppointment(appointmentId);

    }

    @Test
    @SneakyThrows
    @DisplayName("Appointments - Controller - Get by doctor")
    public void getByDoctorTest() {
        List<Appointments> appointments = List.of(getAppointmentForTest());
        Mockito.when(appointmentsService.getActiveAppointmentsByDoctor(3L)).thenReturn(appointments);

        String expected = mapper.writeValueAsString(
                appointments.stream().map(AppointmentResponseDto::new).toList()
        );

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/appointments/doctor/3"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(expected));
    }

    @Test
    @SneakyThrows
    @DisplayName("Appointments - Controller - Get by patient")
    public void getByPatientTest() {
        List<Appointments> appointments = List.of(getAppointmentForTest());
        Mockito.when(appointmentsService.getActiveAppointmentsByPatient(4L)).thenReturn(appointments);

        String expected = mapper.writeValueAsString(
                appointments.stream().map(AppointmentResponseDto::new).toList()
        );

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/appointments/patient/4"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(expected));
    }

    private Appointments getAppointmentForTest() {
        Patients patients = new Patients(2L, "name", null, null, null, false, 2L);
        return new Appointments(
                1L,
                LocalDateTime.now(),
                new Doctors(1L, "doc", null, null, false),
                patients,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                false,
                new PatientCards(10L, null, null, null, false, patients),
                123456L,
                new Offices(5L, "A101", false)
        );
    }
    @Test
    @SneakyThrows
    @WithMockUser(roles = "USER")
    @DisplayName("Appointments - Soft delete - Forbidden for USER")
    public void softDeleteForbiddenTest() {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/appointments/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "USER")
    @DisplayName("Appointments - Restore - Forbidden for USER")
    public void restoreForbiddenTest() {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/appointments/1/restore"))
                .andExpect(status().isForbidden());
    }

}
