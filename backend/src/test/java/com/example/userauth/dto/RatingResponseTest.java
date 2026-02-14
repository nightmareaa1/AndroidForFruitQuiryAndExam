package com.example.userauth.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("RatingResponse DTO Tests")
class RatingResponseTest {

    @Test
    @DisplayName("Should create RatingResponse with no-args constructor")
    void shouldCreateWithNoArgsConstructor() {
        RatingResponse response = new RatingResponse();

        assertNull(response.getId());
        assertNull(response.getCompetitionId());
        assertNull(response.getScores());
    }

    @Test
    @DisplayName("Should create RatingResponse with all fields constructor")
    void shouldCreateWithAllFieldsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        List<RatingResponse.ScoreResponse> scores = List.of(
            new RatingResponse.ScoreResponse(1L, "Taste", 40, new BigDecimal("85.5"))
        );

        RatingResponse response = new RatingResponse(
            1L, 2L, 3L, "Entry", 4L, "Judge", scores, "Good", now
        );

        assertEquals(1L, response.getId());
        assertEquals(2L, response.getCompetitionId());
        assertEquals(3L, response.getEntryId());
        assertEquals("Entry", response.getEntryName());
        assertEquals(4L, response.getJudgeId());
        assertEquals("Judge", response.getJudgeName());
        assertEquals(scores, response.getScores());
        assertEquals("Good", response.getNote());
        assertEquals(now, response.getSubmittedAt());
    }

    @Test
    @DisplayName("Should set and get all fields")
    void shouldSetAndGetAllFields() {
        RatingResponse response = new RatingResponse();
        LocalDateTime now = LocalDateTime.now();
        List<RatingResponse.ScoreResponse> scores = List.of();

        response.setId(5L);
        response.setCompetitionId(6L);
        response.setEntryId(7L);
        response.setEntryName("Test");
        response.setJudgeId(8L);
        response.setJudgeName("Judge");
        response.setScores(scores);
        response.setNote("Note");
        response.setSubmittedAt(now);

        assertEquals(5L, response.getId());
        assertEquals(6L, response.getCompetitionId());
        assertEquals("Test", response.getEntryName());
        assertEquals("Judge", response.getJudgeName());
    }

    @Test
    @DisplayName("Should test ScoreResponse")
    void shouldTestScoreResponse() {
        RatingResponse.ScoreResponse score = new RatingResponse.ScoreResponse();

        score.setParameterId(1L);
        score.setParameterName("Taste");
        score.setParameterWeight(40);
        score.setScore(new BigDecimal("88.0"));

        assertEquals(1L, score.getParameterId());
        assertEquals("Taste", score.getParameterName());
        assertEquals(40, score.getParameterWeight());
        assertEquals(new BigDecimal("88.0"), score.getScore());
    }

    @Test
    @DisplayName("Should create ScoreResponse with constructor")
    void shouldCreateScoreResponseWithConstructor() {
        RatingResponse.ScoreResponse score = new RatingResponse.ScoreResponse(
            2L, "Color", 30, new BigDecimal("92.5")
        );

        assertEquals(2L, score.getParameterId());
        assertEquals("Color", score.getParameterName());
        assertEquals(30, score.getParameterWeight());
        assertEquals(new BigDecimal("92.5"), score.getScore());
    }
}
