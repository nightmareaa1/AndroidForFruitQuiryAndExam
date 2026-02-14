package com.example.userauth.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("RatingRequest DTO Tests")
class RatingRequestTest {

    @Test
    @DisplayName("Should create RatingRequest with no-args constructor")
    void shouldCreateWithNoArgsConstructor() {
        RatingRequest request = new RatingRequest();

        assertNull(request.getCompetitionId());
        assertNull(request.getEntryId());
        assertNull(request.getScores());
        assertNull(request.getNote());
    }

    @Test
    @DisplayName("Should create RatingRequest with all fields constructor")
    void shouldCreateWithAllFieldsConstructor() {
        List<RatingRequest.ScoreRequest> scores = List.of(
            new RatingRequest.ScoreRequest(1L, new BigDecimal("85.5"))
        );

        RatingRequest request = new RatingRequest(1L, 2L, scores, "Good job");

        assertEquals(1L, request.getCompetitionId());
        assertEquals(2L, request.getEntryId());
        assertEquals(scores, request.getScores());
        assertEquals("Good job", request.getNote());
    }

    @Test
    @DisplayName("Should set and get all fields")
    void shouldSetAndGetAllFields() {
        RatingRequest request = new RatingRequest();
        List<RatingRequest.ScoreRequest> scores = List.of();

        request.setCompetitionId(3L);
        request.setEntryId(4L);
        request.setScores(scores);
        request.setNote("Note");

        assertEquals(3L, request.getCompetitionId());
        assertEquals(4L, request.getEntryId());
        assertEquals(scores, request.getScores());
        assertEquals("Note", request.getNote());
    }

    @Test
    @DisplayName("Should test ScoreRequest")
    void shouldTestScoreRequest() {
        RatingRequest.ScoreRequest score = new RatingRequest.ScoreRequest();

        score.setParameterId(1L);
        score.setScore(new BigDecimal("90.0"));

        assertEquals(1L, score.getParameterId());
        assertEquals(new BigDecimal("90.0"), score.getScore());
    }

    @Test
    @DisplayName("Should create ScoreRequest with constructor")
    void shouldCreateScoreRequestWithConstructor() {
        RatingRequest.ScoreRequest score = new RatingRequest.ScoreRequest(2L, new BigDecimal("75.5"));

        assertEquals(2L, score.getParameterId());
        assertEquals(new BigDecimal("75.5"), score.getScore());
    }
}
