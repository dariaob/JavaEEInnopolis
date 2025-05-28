package org.dariaob.controller_tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.dariaob.Attestation03Application;
import org.dariaob.controllers.SpecializationsController;
import org.dariaob.dto.specializations.SpecializationDto;
import org.dariaob.models.Specializations;
import org.dariaob.repositories.UsersRepository;
import org.dariaob.security.jwt.JwtFilter;
import org.dariaob.security.jwt.JwtService;
import org.dariaob.security.users.UsersDetailsServiceImpl;
import org.dariaob.services.SpecializationsService;
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

import java.util.List;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {
        SpecializationsController.class,
        JwtService.class,
        UsersDetailsServiceImpl.class,
        JwtFilter.class,
        UsersRepository.class
})
@ContextConfiguration(classes = Attestation03Application.class)
@TestPropertySource(locations = "classpath:application-test.properties")
public class SpecializationsControllerTest {

    @Autowired
    private WebApplicationContext context;

    @MockitoBean
    private SpecializationsService specializationsService;

    private final ObjectMapper mapper = new ObjectMapper();
    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("Specializations - Controller - Get all active")
    public void getAllActiveTest() throws Exception {
        Specializations spec = createTestSpecialization(1L, "Кардиология");
        Mockito.when(specializationsService.getAllActive()).thenReturn(List.of(spec));

        String expected = mapper.writeValueAsString(List.of(new SpecializationDto(spec)));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/specializations"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(expected));
    }

    @Test
    @DisplayName("Specializations - Controller - Get by id")
    @WithMockUser
    public void getByIdTest() throws Exception {
        Specializations spec = createTestSpecialization(1L, "Неврология");
        Mockito.when(specializationsService.getActiveById(1L)).thenReturn(spec);

        String expected = mapper.writeValueAsString(new SpecializationDto(spec));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/specializations/1"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(expected));
    }

    @Test
    @DisplayName("Specializations - Controller - Get by name")
    @WithMockUser
    public void getByNameTest() throws Exception {
        Specializations spec = createTestSpecialization(1L, "Терапия");
        Mockito.when(specializationsService.getByNameIgnoreCase("Терапия")).thenReturn(spec);

        String expected = mapper.writeValueAsString(new SpecializationDto(spec));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/specializations/by-name/Терапия"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(expected));
    }

    @Test
    @DisplayName("Specializations - Controller - Search by name")
    public void searchByNameTest() throws Exception {
        Specializations spec = createTestSpecialization(1L, "Педиатрия");
        Mockito.when(specializationsService.searchByName("педи")).thenReturn(List.of(spec));

        String expected = mapper.writeValueAsString(List.of(new SpecializationDto(spec)));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/specializations/search?namePart=педи"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(expected));
    }

    @Test
    @DisplayName("Specializations - Controller - Create specialization")
    @WithMockUser(roles = "ADMIN")
    public void createTest() throws Exception {
        SpecializationDto dto = new SpecializationDto();
        dto.setName("Хирургия");

        Specializations spec = createTestSpecialization(1L, "Хирургия");
        Mockito.when(specializationsService.save(Mockito.any())).thenReturn(spec);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/specializations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Хирургия"));
    }

    @Test
    @DisplayName("Specializations - Controller - Update specialization")
    @WithMockUser(roles = "ADMIN")
    public void updateTest() throws Exception {
        SpecializationDto dto = new SpecializationDto();
        dto.setName("Обновленная");

        Specializations existing = createTestSpecialization(1L, "Старая");
        Specializations updated = createTestSpecialization(1L, "Обновленная");

        Mockito.when(specializationsService.getActiveById(1L)).thenReturn(existing);
        Mockito.when(specializationsService.save(Mockito.any())).thenReturn(updated);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/specializations/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Обновленная"));
    }

    @Test
    @DisplayName("Specializations - Controller - Soft delete")
    @WithMockUser(roles = "ADMIN")
    public void softDeleteTest() throws Exception {
        Mockito.doNothing().when(specializationsService).softDelete(1L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/specializations/1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Specializations - Controller - Restore")
    @WithMockUser(roles = "ADMIN")
    public void restoreTest() throws Exception {
        Mockito.doNothing().when(specializationsService).restore(1L);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/specializations/restore/1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Specializations - Controller - Create forbidden for USER")
    @WithMockUser(roles = "USER")
    public void createForbiddenTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/specializations"))
                .andExpect(status().isForbidden());
    }

    private Specializations createTestSpecialization(Long id, String name) {
        Specializations spec = new Specializations();
        spec.setId(id);
        spec.setName(name);
        spec.setDeleted(false);
        return spec;
    }
}