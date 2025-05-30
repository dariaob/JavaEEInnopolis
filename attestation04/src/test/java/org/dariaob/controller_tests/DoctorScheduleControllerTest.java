package org.dariaob.controller_tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import org.dariaob.Attestation04Application;
import org.dariaob.TestWithContainer;
import org.dariaob.controllers.DoctorScheduleController;
import org.dariaob.dto.doctorSchedule.DoctorScheduleDto;
import org.dariaob.models.DoctorSchedule;
import org.dariaob.models.Doctors;
import org.dariaob.models.Offices;
import org.dariaob.repositories.UsersRepository;
import org.dariaob.security.jwt.JwtFilter;
import org.dariaob.security.jwt.JwtService;
import org.dariaob.security.users.UsersDetailsServiceImpl;
import org.dariaob.services.DoctorScheduleService;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * The type Doctor schedule controller test.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {
        DoctorScheduleController.class,
        JwtService.class,
        UsersDetailsServiceImpl.class,
        JwtFilter.class,
        UsersRepository.class
})
@ContextConfiguration(classes = Attestation04Application.class)
@TestPropertySource(locations = "classpath:application-test.properties")
public class DoctorScheduleControllerTest extends TestWithContainer {

    @Autowired
    private WebApplicationContext context;

    @MockitoBean
    private DoctorScheduleService doctorScheduleService;

    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private MockMvc mockMvc;

    /**
     * Sets .
     */
    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    /**
     * Gets all active test.
     */
    @Test
    @SneakyThrows
    @DisplayName("Get all active doctor schedules")
    public void getAllActiveTest() {
        List<DoctorSchedule> schedules = List.of(getTestSchedule());
        Mockito.when(doctorScheduleService.getAllActive()).thenReturn(schedules);

        String expected = mapper.writeValueAsString(
                schedules.stream().map(DoctorScheduleDto::fromEntity).toList()
        );

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/doctor-schedules"))
                .andExpect(status().isOk())
                .andExpect(content().string(expected));
    }

    /**
     * Gets active by id test.
     */
    @Test
    @SneakyThrows
    @DisplayName("Get doctor schedule by ID")
    public void getActiveByIdTest() {
        DoctorSchedule schedule = getTestSchedule();
        Mockito.when(doctorScheduleService.getActiveById(1L)).thenReturn(Optional.of(schedule));

        String expected = mapper.writeValueAsString(DoctorScheduleDto.fromEntity(schedule));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/doctor-schedules/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(expected));
    }

    /**
     * Gets by doctor test.
     */
    @Test
    @SneakyThrows
    @DisplayName("Get doctor schedules by doctor ID")
    public void getByDoctorTest() {
        List<DoctorSchedule> schedules = List.of(getTestSchedule());
        Mockito.when(doctorScheduleService.getByDoctor(2L)).thenReturn(schedules);

        String expected = mapper.writeValueAsString(
                schedules.stream().map(DoctorScheduleDto::fromEntity).toList()
        );

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/doctor-schedules/by-doctor/2"))
                .andExpect(status().isOk())
                .andExpect(content().string(expected));
    }

    /**
     * Gets by doctor and day test.
     */
    @Test
    @SneakyThrows
    @DisplayName("Get doctor schedules by doctor and day")
    public void getByDoctorAndDayTest() {
        List<DoctorSchedule> schedules = List.of(getTestSchedule());
        Mockito.when(doctorScheduleService.getByDoctorAndDay(2L, (short) 1)).thenReturn(schedules);

        String expected = mapper.writeValueAsString(
                schedules.stream().map(DoctorScheduleDto::fromEntity).toList()
        );

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/doctor-schedules/by-doctor-day/2/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(expected));
    }

    /**
     * Create schedule test.
     */
    @Test
    @SneakyThrows
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Create doctor schedule")
    public void createScheduleTest() {
        DoctorSchedule schedule = getTestSchedule();
        DoctorScheduleDto dto = DoctorScheduleDto.fromEntity(schedule);
        Mockito.when(doctorScheduleService.create(Mockito.any())).thenReturn(schedule);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/doctor-schedules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(dto)));
    }

    /**
     * Update schedule test.
     */
    @Test
    @SneakyThrows
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Update doctor schedule")
    public void updateScheduleTest() {
        DoctorSchedule schedule = getTestSchedule();
        DoctorScheduleDto dto = DoctorScheduleDto.fromEntity(schedule);
        Mockito.when(doctorScheduleService.update(Mockito.eq(1L), Mockito.any())).thenReturn(schedule);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/doctor-schedules/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(dto)));
    }

    /**
     * Delete schedule test.
     */
    @Test
    @SneakyThrows
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Delete doctor schedule")
    public void deleteScheduleTest() {
        Mockito.doNothing().when(doctorScheduleService).delete(1L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/doctor-schedules/1"))
                .andExpect(status().isOk());
    }

    /**
     * Restore schedule test.
     */
    @Test
    @SneakyThrows
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Restore doctor schedule")
    public void restoreScheduleTest() {
        Mockito.doNothing().when(doctorScheduleService).restore(1L);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/doctor-schedules/restore/1"))
                .andExpect(status().isOk());
    }

    private DoctorSchedule getTestSchedule() {
        Doctors doctor = new Doctors(1L, "Test Doctor", null, null, false);
        Offices office = new Offices(1L, "Test Office", false);
        return new DoctorSchedule(
                1L,
                doctor,
                (short) 1,
                LocalTime.of(9, 0),
                LocalTime.of(13, 0),
                office,
                false
        );
    }
}
