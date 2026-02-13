package com.example.userauth.controller;

import com.example.userauth.config.TestConfig;
import com.example.userauth.dto.*;
import com.example.userauth.entity.User;
import com.example.userauth.repository.UserRepository;
import com.example.userauth.service.CompetitionService;
import com.example.userauth.service.RatingDataService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CompetitionController.class)
@ContextConfiguration(classes = {CompetitionController.class, TestConfig.class})
@DisplayName("CompetitionController Tests")
class CompetitionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CompetitionService competitionService;

    @MockBean
    private RatingDataService ratingDataService;

    @MockBean
    private UserRepository userRepository;

    private CompetitionRequest validRequest;
    private CompetitionResponse responseDto;
    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new User("testuser", "password", false);
        mockUser.setId(1L);

        validRequest = createValidRequest();
        responseDto = createResponseDto();
    }

    private CompetitionRequest createValidRequest() {
        CompetitionRequest request = new CompetitionRequest();
        request.setName("Test Competition");
        request.setDescription("Test Description");
        request.setModelId(1L);
        request.setDeadline(LocalDateTime.now().plusDays(7));
        return request;
    }

    private CompetitionResponse createResponseDto() {
        CompetitionResponse dto = new CompetitionResponse();
        dto.setId(1L);
        dto.setName("Test Competition");
        dto.setDescription("Test Description");
        dto.setModelId(1L);
        dto.setCreatorId(1L);
        dto.setCreatorUsername("testuser");
        dto.setStatus("ACTIVE");
        return dto;
    }

    @Test
    @DisplayName("Should get all competitions successfully")
    void getAllCompetitions_Success() throws Exception {
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(mockUser));
        when(competitionService.getAllCompetitions(anyLong(), anyBoolean()))
                .thenReturn(Arrays.asList(responseDto));

        mockMvc.perform(get("/api/competitions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Test Competition"));
    }

    @Test
    @DisplayName("Should return empty list when no competitions")
    void getAllCompetitions_Empty() throws Exception {
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(mockUser));
        when(competitionService.getAllCompetitions(anyLong(), anyBoolean()))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/competitions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("Should get competition by ID successfully")
    void getCompetitionById_Success() throws Exception {
        when(competitionService.getCompetitionById(1L)).thenReturn(Optional.of(responseDto));

        mockMvc.perform(get("/api/competitions/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Competition"));
    }

    @Test
    @DisplayName("Should return 404 when competition not found")
    void getCompetitionById_NotFound() throws Exception {
        when(competitionService.getCompetitionById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/competitions/{id}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should create competition successfully")
    void createCompetition_Success() throws Exception {
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(mockUser));
        when(competitionService.createCompetition(any(), anyLong())).thenReturn(responseDto);

        mockMvc.perform(post("/api/competitions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Competition"));
    }

    @Test
    @DisplayName("Should return 400 when creating with invalid data")
    void createCompetition_InvalidData() throws Exception {
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(mockUser));
        when(competitionService.createCompetition(any(), anyLong()))
                .thenThrow(new IllegalArgumentException("Invalid data"));

        mockMvc.perform(post("/api/competitions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should update competition successfully")
    void updateCompetition_Success() throws Exception {
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(mockUser));
        when(competitionService.updateCompetition(anyLong(), any(), anyLong())).thenReturn(responseDto);

        mockMvc.perform(put("/api/competitions/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("Should delete competition successfully")
    void deleteCompetition_Success() throws Exception {
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(mockUser));
        doNothing().when(competitionService).deleteCompetition(anyLong(), anyLong());

        mockMvc.perform(delete("/api/competitions/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should export competition data successfully")
    void exportCompetition_Success() throws Exception {
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(mockUser));
        when(ratingDataService.canExportRatingData(anyLong(), anyLong())).thenReturn(true);
        when(ratingDataService.generateCompetitionCSV(anyLong())).thenReturn("csv,data");

        mockMvc.perform(get("/api/competitions/{id}/export", 1L))
                .andExpect(status().isOk())
                .andExpect(content().string("csv,data"));
    }
}
