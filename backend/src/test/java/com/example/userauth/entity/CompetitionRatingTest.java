package com.example.userauth.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CompetitionRating Entity Tests")
class CompetitionRatingTest {

    @Test
    @DisplayName("Should create CompetitionRating with no-args constructor")
    void shouldCreateCompetitionRatingWithNoArgsConstructor() {
        CompetitionRating rating = new CompetitionRating();

        assertNull(rating.getId());
        assertNull(rating.getScore());
    }

    @Test
    @DisplayName("Should create CompetitionRating with required fields constructor")
    void shouldCreateCompetitionRatingWithRequiredFields() {
        Competition competition = new Competition();
        CompetitionEntry entry = new CompetitionEntry();
        User judge = new User();
        EvaluationParameter parameter = new EvaluationParameter();
        BigDecimal score = new BigDecimal("85.50");
        String note = "Great work!";

        CompetitionRating rating = new CompetitionRating(
            competition, entry, judge, parameter, score, note
        );

        assertEquals(competition, rating.getCompetition());
        assertEquals(entry, rating.getEntry());
        assertEquals(judge, rating.getJudge());
        assertEquals(parameter, rating.getParameter());
        assertEquals(score, rating.getScore());
        assertEquals(note, rating.getNote());
    }

    @Test
    @DisplayName("Should set and get all fields")
    void shouldSetAndGetAllFields() {
        CompetitionRating rating = new CompetitionRating();
        Competition competition = new Competition();
        CompetitionEntry entry = new CompetitionEntry();
        EvaluationParameter parameter = new EvaluationParameter();
        User judge = new User();
        LocalDateTime now = LocalDateTime.now();

        rating.setId(1L);
        rating.setCompetition(competition);
        rating.setEntry(entry);
        rating.setParameter(parameter);
        rating.setJudge(judge);
        rating.setScore(new BigDecimal("85.50"));
        rating.setNote("Excellent!");
        rating.setSubmittedAt(now);

        assertEquals(1L, rating.getId());
        assertEquals(competition, rating.getCompetition());
        assertEquals(entry, rating.getEntry());
        assertEquals(parameter, rating.getParameter());
        assertEquals(judge, rating.getJudge());
        assertEquals(new BigDecimal("85.50"), rating.getScore());
        assertEquals("Excellent!", rating.getNote());
        assertEquals(now, rating.getSubmittedAt());
    }

    @Test
    @DisplayName("Should handle soft delete")
    void shouldHandleSoftDelete() {
        CompetitionRating rating = new CompetitionRating();
        assertFalse(rating.isDeleted());

        rating.setDeletedAt(LocalDateTime.now());
        assertTrue(rating.isDeleted());
    }
}
