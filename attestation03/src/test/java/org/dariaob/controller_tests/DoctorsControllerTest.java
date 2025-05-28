package org.dariaob.controller_tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.dariaob.TestWithContainer;
import org.dariaob.controllers.DoctorsController;
import org.dariaob.dto.doctors.DoctorRequestDto;
import org.dariaob.exceptions.DataNotFoundException;
import org.dariaob.models.*;
import org.dariaob.services.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for {@link DoctorsController} without cache testing
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class DoctorsControllerTest extends TestWithContainer {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;
    private final ObjectMapper mapper = new ObjectMapper();

    @MockBean
    private DoctorsService doctorsService;
    @MockBean
    private OfficesService officesService;
    @MockBean
    private SpecializationsService specializationsService;
    @MockBean
    private DoctorSpecializationsService doctorSpecializationsService;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    private Doctors createTestDoctor(boolean withNullFields) {
        Doctors doctor = new Doctors();
        doctor.setId(1L);
        doctor.setName("Dr. Test");
        doctor.setPhone("123456789");

        if (!withNullFields) {
            doctor.setWorkHoursFrom(LocalDateTime.now());
            doctor.setWorkHoursFor(LocalDateTime.now().plusHours(8));
            doctor.setOffice(new Offices(1L, "A101", false));

            DoctorSpecializations spec = new DoctorSpecializations();
            Specializations specialization = new Specializations(1L, "Кардиолог", "Heart disease cure and diagnostics", false);
            spec.setSpecialization(specialization);
            doctor.setDoctorSpecializations(Collections.singleton(spec));
        }
        return doctor;
    }

    @Test
    @DisplayName("Doctors - Controller - Get by id - Success")
    void getDoctorByIdTest() throws Exception {
        Doctors doctor = createTestDoctor(false);
        when(doctorsService.getActiveById(1L)).thenReturn(doctor);

        mockMvc.perform(get("/api/v1/doctors/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Dr. Test")))
                .andExpect(jsonPath("$.workHoursFrom", notNullValue()))
                .andExpect(jsonPath("$.office.id", is(1)))
                .andExpect(jsonPath("$.specializations", hasSize(1)));
    }

    @Test
    @DisplayName("Doctors - Controller - Get by id - Not Found")
    void getDoctorByIdNotFoundTest() throws Exception {
        when(doctorsService.getActiveById(anyLong()))
                .thenThrow(new DataNotFoundException("Doctor not found"));

        mockMvc.perform(get("/api/v1/doctors/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("not found")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Doctors - Controller - Create doctor - Success")
    void createDoctorTest() throws Exception {
        DoctorRequestDto requestDto = new DoctorRequestDto();
        requestDto.setName("Dr. New");
        requestDto.setPhone("987654321");
        requestDto.setOfficeId(1L);
        requestDto.setSpecializationIds(Set.of(1L));

        Offices office = new Offices(1L, "A101", false);
        Specializations spec = new Specializations(1L, "Кардиолог", "Heart disease cure and diagnostics", false);
        Doctors savedDoctor = requestDto.toEntity();
        savedDoctor.setId(2L);
        savedDoctor.setOffice(office);

        when(officesService.getActiveOfficeById(1L)).thenReturn(office);
        when(specializationsService.getActiveById(1L)).thenReturn(spec);
        when(doctorsService.save(any(Doctors.class))).thenReturn(savedDoctor);

        mockMvc.perform(post("/api/v1/doctors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.name", is("Dr. New")));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Doctors - Controller - Create doctor - Forbidden for USER")
    void createDoctorForbiddenTest() throws Exception {
        mockMvc.perform(post("/api/v1/doctors"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Doctors - Controller - Get all doctors - Success")
    void getAllDoctorsTest() throws Exception {
        Doctors doctor = createTestDoctor(false);
        when(doctorsService.getAllActive()).thenReturn(List.of(doctor));

        mockMvc.perform(get("/api/v1/doctors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Dr. Test")));
    }
}
