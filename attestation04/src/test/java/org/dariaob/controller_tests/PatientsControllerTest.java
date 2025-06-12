package org.dariaob.controller_tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.dariaob.Attestation04Application;
import org.dariaob.controllers.PatientsController;
import org.dariaob.dto.patients.PatientDto;
import org.dariaob.models.PatientCards;
import org.dariaob.models.Patients;
import org.dariaob.repositories.UsersRepository;
import org.dariaob.security.jwt.JwtFilter;
import org.dariaob.security.jwt.JwtService;
import org.dariaob.security.users.UsersDetailsServiceImpl;
import org.dariaob.services.PatientCardsService;
import org.dariaob.services.PatientsService;
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

import java.time.LocalDate;
import java.util.List;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * The type Patients controller test.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {
        PatientsController.class,
        JwtService.class,
        UsersDetailsServiceImpl.class,
        JwtFilter.class,
        UsersRepository.class
})
@ContextConfiguration(classes = Attestation04Application.class)
@TestPropertySource(locations = "classpath:application-test.properties")
public class PatientsControllerTest {

    @Autowired
    private WebApplicationContext context;

    @MockitoBean
    private PatientsService patientsService;

    @MockitoBean
    private PatientCardsService patientCardsService;

    private final ObjectMapper mapper =  new ObjectMapper()
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
     * Gets all active patients test.
     *
     * @throws Exception the exception
     */
    @Test
    @DisplayName("Patients - Controller - Get all active")
    public void getAllActivePatientsTest() throws Exception {
        Patients patient = getTestPatient();
        Mockito.when(patientsService.getAllActive()).thenReturn(List.of(patient));

        String expected = mapper.writeValueAsString(List.of(convertToDto(patient)));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/patients"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(expected));
    }

    /**
     * Gets patient by id test.
     *
     * @throws Exception the exception
     */
    @Test
    @DisplayName("Patients - Controller - Get by id")
    @WithMockUser
    public void getPatientByIdTest() throws Exception {
        Patients patient = getTestPatient();
        Mockito.when(patientsService.getActiveById(1L)).thenReturn(patient);

        String expected = mapper.writeValueAsString(convertToDto(patient));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/patients/1"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(expected));
    }

    /**
     * Gets patient by phone test.
     *
     * @throws Exception the exception
     */
    @Test
    @DisplayName("Patients - Controller - Get by phone")
    @WithMockUser
    public void getPatientByPhoneTest() throws Exception {
        Patients patient = getTestPatient();
        Mockito.when(patientsService.getActiveByPhone("+79991112233")).thenReturn(patient);

        String expected = mapper.writeValueAsString(convertToDto(patient));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/patients/by-phone/+79991112233"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(expected));
    }

    /**
     * Create patient test.
     *
     * @throws Exception the exception
     */
    @Test
    @DisplayName("Patients - Controller - Create patient")
    @WithMockUser(roles = "ADMIN")
    public void createPatientTest() throws Exception {
        Patients patient = getTestPatient();
        PatientDto patientDto = convertToDto(patient);

        Mockito.when(patientsService.save(Mockito.any(Patients.class))).thenReturn(patient);
        Mockito.when(patientCardsService.getActiveById(1L)).thenReturn(new PatientCards());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(patientDto)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(patient.getName()));
    }

    /**
     * Update patient test.
     *
     * @throws Exception the exception
     */
    @Test
    @DisplayName("Patients - Controller - Update patient")
    @WithMockUser(roles = "ADMIN")
    public void updatePatientTest() throws Exception {
        Patients patient = getTestPatient();
        PatientDto patientDto = convertToDto(patient);
        patientDto.setName("Updated Name");

        Mockito.when(patientsService.getActiveById(1L)).thenReturn(patient);
        Mockito.when(patientsService.save(Mockito.any(Patients.class))).thenReturn(patient);
        Mockito.when(patientCardsService.getActiveById(1L)).thenReturn(new PatientCards());

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/patients/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(patientDto)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(patient.getName()));
    }

    /**
     * Soft delete patient test.
     *
     * @throws Exception the exception
     */
    @Test
    @DisplayName("Patients - Controller - Soft delete")
    @WithMockUser(roles = "ADMIN")
    public void softDeletePatientTest() throws Exception {
        Mockito.doNothing().when(patientsService).softDelete(1L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/patients/1"))
                .andExpect(status().isOk());
    }

    /**
     * Restore patient test.
     *
     * @throws Exception the exception
     */
    @Test
    @DisplayName("Patients - Controller - Restore")
    @WithMockUser(roles = "ADMIN")
    public void restorePatientTest() throws Exception {
        Mockito.doNothing().when(patientsService).restore(1L);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/patients/restore/1"))
                .andExpect(status().isOk());
    }

    /**
     * Create patient forbidden test.
     *
     * @throws Exception the exception
     */
    @Test
    @DisplayName("Patients - Controller - Create patient forbidden for USER")
    @WithMockUser(roles = "USER")
    public void createPatientForbiddenTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/patients"))
                .andExpect(status().isForbidden());
    }

    private Patients getTestPatient() {
        Patients patient = new Patients();
        patient.setId(1L);
        patient.setName("Иван Иванов");
        patient.setBirthDate(LocalDate.of(1980, 1, 1));
        patient.setPhone("+79991112233");
        patient.setInsuranceId(987L);
        patient.setDeleted(false);

        PatientCards card = new PatientCards();
        card.setId(1L);
        patient.setPatientCard(card);

        return patient;
    }

    private PatientDto convertToDto(Patients patient) {
        PatientDto dto = new PatientDto();
        dto.setId(patient.getId());
        dto.setName(patient.getName());
        dto.setBirthDate(patient.getBirthDate());
        dto.setPhone(patient.getPhone());
        dto.setPatientCardId(patient.getPatientCard().getId());
        dto.setInsuranceId(patient.getInsuranceId());
        dto.setDeleted(patient.isDeleted());
        return dto;
    }
}
