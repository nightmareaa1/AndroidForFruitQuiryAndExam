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
import java.util.List;
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

    @Test
    @DisplayName("Should get created competitions successfully")
    void getCreatedCompetitions_Success() throws Exception {
        CompetitionResponse competition1 = createResponseDto();
        competition1.setId(1L);
        competition1.setName("Competition 1");
        
        CompetitionResponse competition2 = createResponseDto();
        competition2.setId(2L);
        competition2.setName("Competition 2");
        
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(mockUser));
        when(competitionService.getCompetitionsByCreator(anyLong())).thenReturn(Arrays.asList(competition1, competition2));

        mockMvc.perform(get("/api/competitions/created"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Competition 1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Competition 2"));
    }

    @Test
    @DisplayName("Should return empty list when no created competitions")
    void getCreatedCompetitions_Empty() throws Exception {
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(mockUser));
        when(competitionService.getCompetitionsByCreator(anyLong())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/competitions/created"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("Should get judged competitions successfully")
    void getJudgedCompetitions_Success() throws Exception {
        CompetitionResponse competition1 = createResponseDto();
        competition1.setId(1L);
        competition1.setName("Judged Competition 1");
        
        CompetitionResponse competition2 = createResponseDto();
        competition2.setId(2L);
        competition2.setName("Judged Competition 2");
        
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(mockUser));
        when(competitionService.getCompetitionsByJudge(anyLong())).thenReturn(Arrays.asList(competition1, competition2));

        mockMvc.perform(get("/api/competitions/judged"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Judged Competition 1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Judged Competition 2"));
    }

    @Test
    @DisplayName("Should return empty list when no judged competitions")
    void getJudgedCompetitions_Empty() throws Exception {
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(mockUser));
        when(competitionService.getCompetitionsByJudge(anyLong())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/competitions/judged"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("Should add judges to competition successfully")
    void addJudges_Success() throws Exception {
        List<Long> judgeIds = Arrays.asList(2L, 3L);
        
        doNothing().when(competitionService).addJudgesToCompetition(anyLong(), anyList());

        mockMvc.perform(post("/api/competitions/{id}/judges", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(judgeIds)))
                .andExpect(status().isOk());
        
        verify(competitionService).addJudgesToCompetition(1L, judgeIds);
    }

    @Test
    @DisplayName("Should return 400 when adding judges with invalid request")
    void addJudges_InvalidRequest() throws Exception {
        List<Long> judgeIds = Arrays.asList(2L, 3L);
        
        doThrow(new IllegalArgumentException("Invalid judge IDs"))
                .when(competitionService).addJudgesToCompetition(anyLong(), anyList());

        mockMvc.perform(post("/api/competitions/{id}/judges", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(judgeIds)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should remove judge from competition successfully")
    void removeJudge_Success() throws Exception {
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(mockUser));
        doNothing().when(competitionService).removeJudgeFromCompetition(anyLong(), anyLong(), anyLong());

        mockMvc.perform(delete("/api/competitions/{id}/judges/{judgeId}", 1L, 2L))
                .andExpect(status().isNoContent());
        
        verify(competitionService).removeJudgeFromCompetition(1L, 2L, 1L);
    }

    @Test
    @DisplayName("Should return 400 when removing judge with invalid request")
    void removeJudge_InvalidRequest() throws Exception {
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(mockUser));
        doThrow(new IllegalArgumentException("Judge not found"))
                .when(competitionService).removeJudgeFromCompetition(anyLong(), anyLong(), anyLong());

        mockMvc.perform(delete("/api/competitions/{id}/judges/{judgeId}", 1L, 2L))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should update entry status successfully")
    void updateEntryStatus_Success() throws Exception {
        EntryStatusUpdateRequest request = new EntryStatusUpdateRequest();
        request.setStatus("APPROVED");
        
        doNothing().when(competitionService).updateEntryStatus(anyLong(), anyString());

        mockMvc.perform(put("/api/competitions/{competitionId}/entries/{entryId}/status", 1L, 2L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
        
        verify(competitionService).updateEntryStatus(2L, "APPROVED");
    }

    @Test
    @DisplayName("Should return 400 when updating entry status with invalid request")
    void updateEntryStatus_InvalidRequest() throws Exception {
        EntryStatusUpdateRequest request = new EntryStatusUpdateRequest();
        request.setStatus("APPROVED");
        
        doThrow(new IllegalArgumentException("Invalid status"))
                .when(competitionService).updateEntryStatus(anyLong(), anyString());

        mockMvc.perform(put("/api/competitions/{competitionId}/entries/{entryId}/status", 1L, 2L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should get competition entries successfully")
    void getCompetitionEntries_Success() throws Exception {
        CompetitionResponse.EntryResponse entry1 = new CompetitionResponse.EntryResponse();
        entry1.setId(1L);
        entry1.setEntryName("Entry 1");
        
        CompetitionResponse.EntryResponse entry2 = new CompetitionResponse.EntryResponse();
        entry2.setId(2L);
        entry2.setEntryName("Entry 2");
        
        when(competitionService.getCompetitionEntries(anyLong()))
                .thenReturn(Arrays.asList(entry1, entry2));

        mockMvc.perform(get("/api/competitions/{id}/entries", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].entryName").value("Entry 1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].entryName").value("Entry 2"));
    }

    @Test
    @DisplayName("Should return empty list when no competition entries")
    void getCompetitionEntries_Empty() throws Exception {
        when(competitionService.getCompetitionEntries(anyLong()))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/competitions/{id}/entries", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("Should delete entry successfully")
    void deleteEntry_Success() throws Exception {
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(mockUser));
        doNothing().when(competitionService).deleteEntry(anyLong(), anyLong(), anyBoolean());

        mockMvc.perform(delete("/api/competitions/{competitionId}/entries/{entryId}", 1L, 2L))
                .andExpect(status().isNoContent());
        
        verify(competitionService).deleteEntry(2L, 1L, false);
    }

    @Test
    @DisplayName("Should return 400 when deleting entry with invalid request")
    void deleteEntry_InvalidRequest() throws Exception {
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(mockUser));
        doThrow(new IllegalArgumentException("Entry not found"))
                .when(competitionService).deleteEntry(anyLong(), anyLong(), anyBoolean());

        mockMvc.perform(delete("/api/competitions/{competitionId}/entries/{entryId}", 1L, 2L))
                .andExpect(status().isBadRequest());
    }
}
