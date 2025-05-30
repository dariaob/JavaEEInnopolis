package org.dariaob.controller_tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.dariaob.Attestation04Application;
import org.dariaob.controllers.PatientCardsController;
import org.dariaob.dto.patientCards.PatientCardRequestDto;
import org.dariaob.dto.patientCards.PatientCardResponseDto;
import org.dariaob.models.PatientCards;
import org.dariaob.models.PatientCardsHistory;
import org.dariaob.repositories.UsersRepository;
import org.dariaob.security.jwt.JwtFilter;
import org.dariaob.security.jwt.JwtService;
import org.dariaob.security.users.UsersDetailsServiceImpl;
import org.dariaob.services.PatientCardsService;
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

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * The type Patient cards controller test.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {
        PatientCardsController.class,
        JwtService.class,
        UsersDetailsServiceImpl.class,
        JwtFilter.class,
        UsersRepository.class
})
@ContextConfiguration(classes = Attestation04Application.class)
@TestPropertySource(locations = "classpath:application-test.properties")
public class PatientCardsControllerTest {

    @Autowired
    private WebApplicationContext context;

    @MockitoBean
    private PatientCardsService patientCardsService;

    private final ObjectMapper mapper = new ObjectMapper();
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
     * Gets all active cards test.
     *
     * @throws Exception the exception
     */
    @Test
    @DisplayName("PatientCards - Controller - Get all active")
    public void getAllActiveCardsTest() throws Exception {
        PatientCards card = createTestCard(1L, "Головная боль", "Мигрень", "Анальгин");
        Mockito.when(patientCardsService.getAllActive()).thenReturn(List.of(card));

        String expected = mapper.writeValueAsString(List.of(convertToDto(card)));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/patient-cards"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(expected));
    }

    /**
     * Gets card by id test.
     *
     * @throws Exception the exception
     */
    @Test
    @DisplayName("PatientCards - Controller - Get by id")
    @WithMockUser
    public void getCardByIdTest() throws Exception {
        PatientCards card = createTestCard(1L, "Кашель", "Бронхит", "Антибиотики");
        Mockito.when(patientCardsService.getActiveById(1L)).thenReturn(card);

        String expected = mapper.writeValueAsString(convertToDto(card));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/patient-cards/1"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(expected));
    }

    /**
     * Gets card by patient id test.
     *
     * @throws Exception the exception
     */
    @Test
    @DisplayName("PatientCards - Controller - Get by patient id")
    @WithMockUser
    public void getCardByPatientIdTest() throws Exception {
        PatientCards card = createTestCard(1L, "Температура", "Грипп", "Жаропонижающее");
        Mockito.when(patientCardsService.getByPatientId(1L)).thenReturn(card);

        String expected = mapper.writeValueAsString(convertToDto(card));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/patient-cards/by-patient/1"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(expected));
    }

    /**
     * Search by diagnosis test.
     *
     * @throws Exception the exception
     */
    @Test
    @DisplayName("PatientCards - Controller - Search by diagnosis")
    public void searchByDiagnosisTest() throws Exception {
        PatientCards card = createTestCard(1L, null, "Пневмония", null);
        Mockito.when(patientCardsService.findByDiagnosis("пнев")).thenReturn(List.of(card));

        String expected = mapper.writeValueAsString(List.of(convertToDto(card)));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/patient-cards/search?diagnosis=пнев"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(expected));
    }

    /**
     * Create card test.
     *
     * @throws Exception the exception
     */
    @Test
    @DisplayName("PatientCards - Controller - Create card")
    @WithMockUser(roles = "ADMIN")
    public void createCardTest() throws Exception {
        PatientCardRequestDto requestDto = new PatientCardRequestDto();
        requestDto.setSymptoms("Головокружение");
        requestDto.setDiagnosis("Анемия");
        requestDto.setMeds("Препараты железа");

        PatientCards savedCard = createTestCard(1L,
                requestDto.getSymptoms(),
                requestDto.getDiagnosis(),
                requestDto.getMeds());

        Mockito.when(patientCardsService.save(Mockito.any())).thenReturn(savedCard);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/patient-cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.diagnosis").value("Анемия"));
    }

    /**
     * Update card test.
     *
     * @throws Exception the exception
     */
    @Test
    @DisplayName("PatientCards - Controller - Update card")
    @WithMockUser(roles = "ADMIN")
    public void updateCardTest() throws Exception {
        PatientCardRequestDto requestDto = new PatientCardRequestDto();
        requestDto.setSymptoms("Обновленные симптомы");
        requestDto.setDiagnosis("Обновленный диагноз");
        requestDto.setMeds("Обновленные лекарства");

        PatientCards existingCard = createTestCard(1L, "Старые симптомы", "Старый диагноз", "Старые лекарства");
        PatientCards updatedCard = createTestCard(1L,
                requestDto.getSymptoms(),
                requestDto.getDiagnosis(),
                requestDto.getMeds());

        Mockito.when(patientCardsService.getActiveById(1L)).thenReturn(existingCard);
        Mockito.when(patientCardsService.save(Mockito.any())).thenReturn(updatedCard);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/patient-cards/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.diagnosis").value("Обновленный диагноз"));
    }

    /**
     * Soft delete card test.
     *
     * @throws Exception the exception
     */
    @Test
    @DisplayName("PatientCards - Controller - Soft delete")
    @WithMockUser(roles = "ADMIN")
    public void softDeleteCardTest() throws Exception {
        Mockito.doNothing().when(patientCardsService).softDelete(1L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/patient-cards/1"))
                .andExpect(status().isOk());
    }

    /**
     * Restore card test.
     *
     * @throws Exception the exception
     */
    @Test
    @DisplayName("PatientCards - Controller - Restore")
    @WithMockUser(roles = "ADMIN")
    public void restoreCardTest() throws Exception {
        Mockito.doNothing().when(patientCardsService).restore(1L);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/patient-cards/restore/1"))
                .andExpect(status().isOk());
    }

    /**
     * Gets card history test.
     *
     * @throws Exception the exception
     */
    @Test
    @DisplayName("PatientCards - Controller - Get history")
    @WithMockUser
    public void getCardHistoryTest() throws Exception {
        PatientCardsHistory history = new PatientCardsHistory();
        history.setId(1L);
        history.setChangedAt(LocalDateTime.now());

        Mockito.when(patientCardsService.getHistoryByCardId(1L)).thenReturn(List.of(history));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/patient-cards/1/history"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1));
    }

    /**
     * Gets last card change test.
     *
     * @throws Exception the exception
     */
    @Test
    @DisplayName("PatientCards - Controller - Get last change")
    @WithMockUser
    public void getLastCardChangeTest() throws Exception {
        PatientCardsHistory history = new PatientCardsHistory();
        history.setId(1L);
        history.setChangedAt(LocalDateTime.now());

        Mockito.when(patientCardsService.getLastChangeByCardId(1L)).thenReturn(history);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/patient-cards/1/last-change"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1));
    }

    /**
     * Create card forbidden test.
     *
     * @throws Exception the exception
     */
    @Test
    @DisplayName("PatientCards - Controller - Create forbidden for USER")
    @WithMockUser(roles = "USER")
    public void createCardForbiddenTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/patient-cards"))
                .andExpect(status().isForbidden());
    }

    private PatientCards createTestCard(Long id, String symptoms, String diagnosis, String meds) {
        PatientCards card = new PatientCards();
        card.setId(id);
        card.setSymptoms(symptoms);
        card.setDiagnosis(diagnosis);
        card.setMeds(meds);
        card.setDeleted(false);
        return card;
    }

    private PatientCardResponseDto convertToDto(PatientCards card) {
        PatientCardResponseDto dto = new PatientCardResponseDto();
        dto.setId(card.getId());
        dto.setSymptoms(card.getSymptoms());
        dto.setDiagnosis(card.getDiagnosis());
        dto.setMeds(card.getMeds());
        return dto;
    }
}
