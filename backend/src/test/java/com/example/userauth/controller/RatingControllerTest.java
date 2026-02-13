package com.example.userauth.controller;

import com.example.userauth.config.TestConfig;
import com.example.userauth.dto.CompetitionRatingDataResponse;
import com.example.userauth.dto.RatingRequest;
import com.example.userauth.dto.RatingResponse;
import com.example.userauth.entity.User;
import com.example.userauth.repository.UserRepository;
import com.example.userauth.service.RatingDataService;
import com.example.userauth.service.RatingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RatingController.class)
@ContextConfiguration(classes = {RatingController.class, TestConfig.class})
@DisplayName("RatingController Tests")
class RatingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RatingService ratingService;

    @MockBean
    private RatingDataService ratingDataService;

    @MockBean
    private UserRepository userRepository;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new User("testuser", "password", true);
        mockUser.setId(1L);
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(mockUser));
    }

    @Test
    @DisplayName("Should submit rating successfully")
    void submitRating_Success() throws Exception {
        RatingResponse resp = createRatingResponse(1L);
        when(ratingService.submitRating(any(RatingRequest.class), anyLong())).thenReturn(resp);

        RatingRequest req = createRatingRequest();

        mockMvc.perform(post("/api/ratings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Should return 400 when rating request is invalid")
    void submitRating_InvalidArgument() throws Exception {
        when(ratingService.submitRating(any(RatingRequest.class), anyLong()))
                .thenThrow(new IllegalArgumentException("Invalid request"));

        RatingRequest req = createRatingRequest();

        mockMvc.perform(post("/api/ratings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when rating submission not allowed")
    void submitRating_IllegalState() throws Exception {
        when(ratingService.submitRating(any(RatingRequest.class), anyLong()))
                .thenThrow(new IllegalStateException("Not allowed"));

        RatingRequest req = createRatingRequest();

        mockMvc.perform(post("/api/ratings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 500 on server error")
    void submitRating_ServerError() throws Exception {
        when(ratingService.submitRating(any(RatingRequest.class), anyLong()))
                .thenThrow(new RuntimeException("Database error"));

        RatingRequest req = createRatingRequest();

        mockMvc.perform(post("/api/ratings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("Should get ratings by entry successfully")
    void getRatingsByEntry_Success() throws Exception {
        when(ratingService.getRatingsByEntry(2L)).thenReturn(List.of(createRatingResponse(2L)));

        mockMvc.perform(get("/api/ratings/entry/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @DisplayName("Should get ratings by competition successfully")
    void getRatingsByCompetition_Success() throws Exception {
        when(ratingService.getRatingsByCompetition(1L)).thenReturn(List.of(createRatingResponse(3L)));

        mockMvc.perform(get("/api/ratings/competition/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @DisplayName("Should get my ratings successfully")
    void getMyRatings_Success() throws Exception {
        when(ratingService.getRatingsByJudge(anyLong(), anyLong())).thenReturn(List.of(createRatingResponse(4L)));

        mockMvc.perform(get("/api/ratings/competition/1/my-ratings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @DisplayName("Should get rating completion status")
    void getRatingCompletionStatus_Success() throws Exception {
        when(ratingService.hasJudgeCompletedRating(anyLong(), anyLong())).thenReturn(true);

        mockMvc.perform(get("/api/ratings/entry/5/completion-status"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should return 403 when user cannot view rating data")
    void getCompetitionRatingData_Forbidden() throws Exception {
        when(ratingDataService.canViewRatingData(anyLong(), anyLong())).thenReturn(false);

        mockMvc.perform(get("/api/ratings/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should get competition rating data successfully")
    void getCompetitionRatingData_Success() throws Exception {
        CompetitionRatingDataResponse resp = new CompetitionRatingDataResponse(1L, "Test Competition", 1L, Collections.emptyList());
        when(ratingDataService.canViewRatingData(anyLong(), anyLong())).thenReturn(true);
        when(ratingDataService.getCompetitionRatingData(anyLong())).thenReturn(resp);

        mockMvc.perform(get("/api/ratings/1"))
                .andExpect(status().isOk());
    }

    private RatingRequest createRatingRequest() {
        RatingRequest req = new RatingRequest();
        req.setCompetitionId(1L);
        req.setEntryId(2L);
        
        RatingRequest.ScoreRequest score = new RatingRequest.ScoreRequest();
        score.setParameterId(1L);
        score.setScore(new BigDecimal("8.5"));
        req.setScores(List.of(score));
        
        req.setNote("Test note");
        return req;
    }

    private RatingResponse createRatingResponse(Long id) {
        RatingResponse resp = new RatingResponse();
        resp.setId(id);
        resp.setCompetitionId(1L);
        resp.setEntryId(2L);
        resp.setEntryName("Test Entry");
        resp.setJudgeId(3L);
        resp.setJudgeName("Test Judge");
        resp.setScores(Collections.emptyList());
        resp.setNote("Test note");
        return resp;
    }
}
