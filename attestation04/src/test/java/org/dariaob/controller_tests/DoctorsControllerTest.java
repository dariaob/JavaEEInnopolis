package org.dariaob.controller_tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.dariaob.TestWithContainer;
import org.dariaob.controllers.DoctorsController;
import org.dariaob.dto.doctors.DoctorRequestDto;
import org.dariaob.exceptions.DataNotFoundException;
import org.dariaob.models.DoctorSpecializations;
import org.dariaob.models.Doctors;
import org.dariaob.models.Offices;
import org.dariaob.models.Specializations;
import org.dariaob.services.DoctorSpecializationsService;
import org.dariaob.services.DoctorsService;
import org.dariaob.services.OfficesService;
import org.dariaob.services.SpecializationsService;
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

    /**
     * Sets .
     */
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

    /**
     * Gets doctor by id test.
     *
     * @throws Exception the exception
     */
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

    /**
     * Gets doctor by id not found test.
     *
     * @throws Exception the exception
     */
    @Test
    @DisplayName("Doctors - Controller - Get by id - Not Found")
    void getDoctorByIdNotFoundTest() throws Exception {
        when(doctorsService.getActiveById(anyLong()))
                .thenThrow(new DataNotFoundException("Doctor not found"));

        mockMvc.perform(get("/api/v1/doctors/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("not found")));
    }

    /**
     * Create doctor test.
     *
     * @throws Exception the exception
     */
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

    /**
     * Create doctor forbidden test.
     *
     * @throws Exception the exception
     */
    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Doctors - Controller - Create doctor - Forbidden for USER")
    void createDoctorForbiddenTest() throws Exception {
        mockMvc.perform(post("/api/v1/doctors"))
                .andExpect(status().isForbidden());
    }

    /**
     * Gets all doctors test.
     *
     * @throws Exception the exception
     */
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

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Doctors - Controller - Update doctor - Success")
    void updateDoctorTest() throws Exception {
        Doctors existingDoctor = createTestDoctor(false);
        when(doctorsService.getActiveById(1L)).thenReturn(existingDoctor);

        DoctorRequestDto updateDto = new DoctorRequestDto();
        updateDto.setName("Dr. Updated");
        updateDto.setPhone("111222333");
        updateDto.setOfficeId(1L);
        updateDto.setSpecializationIds(Set.of(1L));

        Offices office = new Offices(1L, "A101", false);
        Specializations spec = new Specializations(1L, "Кардиолог", "Description", false);
        when(officesService.getActiveOfficeById(1L)).thenReturn(office);
        when(specializationsService.getActiveById(1L)).thenReturn(spec);

        when(doctorsService.save(any(Doctors.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(put("/api/v1/doctors/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Dr. Updated")))
                .andExpect(jsonPath("$.phone", is("111222333")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Doctors - Controller - Soft delete doctor - Success")
    void deleteDoctorTest() throws Exception {
        doNothing().when(doctorsService).softDelete(1L);

        mockMvc.perform(delete("/api/v1/doctors/1"))
                .andExpect(status().isOk());

        verify(doctorsService, times(1)).softDelete(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Doctors - Controller - Restore doctor - Success")
    void restoreDoctorTest() throws Exception {
        doNothing().when(doctorsService).restore(1L);

        mockMvc.perform(post("/api/v1/doctors/restore/1"))
                .andExpect(status().isOk());

        verify(doctorsService, times(1)).restore(1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Doctors - Controller - Get doctor specializations - Success")
    void getDoctorSpecializationsTest() throws Exception {
        Specializations spec1 = new Specializations(1L, "Кардиолог", "Описание", false);
        Specializations spec2 = new Specializations(2L, "Терапевт", "Описание", false);

        DoctorSpecializations ds1 = new DoctorSpecializations();
        ds1.setSpecialization(spec1);
        DoctorSpecializations ds2 = new DoctorSpecializations();
        ds2.setSpecialization(spec2);

        when(doctorSpecializationsService.getSpecializationsByDoctorId(1L))
                .thenReturn(List.of(ds1, ds2));

        mockMvc.perform(get("/api/v1/doctors/1/specializations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0]").value(1))
                .andExpect(jsonPath("$[1]").value(2));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Doctors - Controller - Add specialization to doctor - Success")
    void addSpecializationToDoctorTest() throws Exception {
        Doctors doctor = createTestDoctor(false);
        Specializations spec = new Specializations(1L, "Кардиолог", "Описание", false);

        when(doctorsService.getActiveById(1L)).thenReturn(doctor);
        when(specializationsService.getActiveById(1L)).thenReturn(spec);
        when(doctorSpecializationsService.existsByDoctorAndSpecialization(1L, 1L)).thenReturn(false);
        when(doctorSpecializationsService.save(any())).thenReturn(null);

        mockMvc.perform(post("/api/v1/doctors/1/specializations/1"))
                .andExpect(status().isOk());

        verify(doctorSpecializationsService, times(1))
                .save(any(DoctorSpecializations.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Doctors - Controller - Remove specialization from doctor - Success")
    void removeSpecializationFromDoctorTest() throws Exception {
        doNothing().when(doctorSpecializationsService).deleteSpecialization(1L, 1L);

        mockMvc.perform(delete("/api/v1/doctors/1/specializations/1"))
                .andExpect(status().isOk());

        verify(doctorSpecializationsService, times(1))
                .deleteSpecialization(1L, 1L);
    }
}
