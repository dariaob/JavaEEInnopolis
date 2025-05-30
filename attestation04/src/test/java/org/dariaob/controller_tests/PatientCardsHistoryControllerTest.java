package org.dariaob.controller_tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import org.dariaob.Attestation04Application;
import org.dariaob.TestWithContainer;
import org.dariaob.controllers.PatientCardsHistoryController;
import org.dariaob.dto.patientCardsHistory.PatientCardHistoryDto;
import org.dariaob.models.PatientCardsHistory;
import org.dariaob.repositories.UsersRepository;
import org.dariaob.security.jwt.JwtFilter;
import org.dariaob.security.jwt.JwtService;
import org.dariaob.security.users.UsersDetailsServiceImpl;
import org.dariaob.services.PatientCardsHistoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
import java.util.Optional;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * The type Patient cards history controller test.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {
        PatientCardsHistoryController.class,
        JwtService.class,
        UsersDetailsServiceImpl.class,
        JwtFilter.class,
        UsersRepository.class
})
@ContextConfiguration(classes = Attestation04Application.class)
@TestPropertySource(locations = "classpath:application-test.properties")
public class PatientCardsHistoryControllerTest extends TestWithContainer {

    @Autowired
    private WebApplicationContext context;

    @MockitoBean
    private PatientCardsHistoryService historyService;

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
     * Gets history by card id test.
     */
    @Test
    @SneakyThrows
    @DisplayName("PatientCardsHistory - Controller - Get history by card ID")
    public void getHistoryByCardIdTest() {
        List<PatientCardsHistory> historyList = List.of(getHistoryEntry());
        Mockito.when(historyService.getByCardId(1L)).thenReturn(historyList);

        List<PatientCardHistoryDto> expectedDto = historyList.stream()
                .map(this::convertToDto)
                .toList();

        String expected = mapper.writeValueAsString(expectedDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/patient-cards/history/1"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(expected));
    }

    /**
     * Gets last change test.
     */
    @Test
    @SneakyThrows
    @DisplayName("PatientCardsHistory - Controller - Get last change by card ID")
    public void getLastChangeTest() {
        PatientCardsHistory lastChange = getHistoryEntry();
        Mockito.when(historyService.getLastChangeByCardId(1L)).thenReturn(Optional.of(lastChange));

        PatientCardHistoryDto expectedDto = convertToDto(lastChange);
        String expected = mapper.writeValueAsString(expectedDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/patient-cards/history/1/last"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(expected));
    }

    /**
     * Gets last change not found test.
     */
    @Test
    @SneakyThrows
    @DisplayName("PatientCardsHistory - Controller - Get last change - Return null if not found")
    public void getLastChangeNotFoundTest() {
        Mockito.when(historyService.getLastChangeByCardId(2L)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/patient-cards/history/2/last"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("")); // т.к. null -> пустая строка
    }

    private PatientCardsHistory getHistoryEntry() {
        PatientCardsHistory history = new PatientCardsHistory();
        history.setId(1L);
        history.setChangedAt(LocalDateTime.of(2024, 4, 1, 10, 0));
        history.setChangedBy("admin");
        history.setOldDiagnosis("Old diagnosis");
        history.setNewDiagnosis("New diagnosis");
        history.setOldMeds("Old meds");
        history.setNewMeds("New meds");
        return history;
    }

    private PatientCardHistoryDto convertToDto(PatientCardsHistory history) {
        PatientCardHistoryDto dto = new PatientCardHistoryDto();
        dto.setId(history.getId());
        dto.setChangedAt(history.getChangedAt());
        dto.setChangedBy(history.getChangedBy());
        dto.setOldDiagnosis(history.getOldDiagnosis());
        dto.setNewDiagnosis(history.getNewDiagnosis());
        dto.setOldMeds(history.getOldMeds());
        dto.setNewMeds(history.getNewMeds());
        return dto;
    }
}

