package com.example.userauth.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Competition Entity Tests")
class CompetitionTest {

    @Test
    @DisplayName("Should create Competition with no-args constructor")
    void shouldCreateCompetitionWithNoArgsConstructor() {
        Competition competition = new Competition();

        assertNull(competition.getId());
        assertNull(competition.getName());
        assertNull(competition.getDescription());
    }

    @Test
    @DisplayName("Should create Competition with required fields constructor")
    void shouldCreateCompetitionWithRequiredFields() {
        EvaluationModel model = new EvaluationModel();
        User creator = new User();
        LocalDateTime deadline = LocalDateTime.now().plusDays(7);

        Competition competition = new Competition(
            "Test Competition",
            "Test Description",
            model,
            creator,
            deadline
        );

        assertEquals("Test Competition", competition.getName());
        assertEquals("Test Description", competition.getDescription());
        assertEquals(model, competition.getModel());
        assertEquals(creator, competition.getCreator());
        assertEquals(deadline, competition.getDeadline());
        assertEquals(Competition.CompetitionStatus.ACTIVE, competition.getStatus());
    }

    @Test
    @DisplayName("Should throw exception when model is null")
    void shouldThrowExceptionWhenModelIsNull() {
        User creator = new User();
        LocalDateTime deadline = LocalDateTime.now();

        assertThrows(IllegalArgumentException.class, () -> {
            new Competition("Test", "Desc", null, creator, deadline);
        });
    }

    @Test
    @DisplayName("Should throw exception when creator is null")
    void shouldThrowExceptionWhenCreatorIsNull() {
        EvaluationModel model = new EvaluationModel();
        LocalDateTime deadline = LocalDateTime.now();

        assertThrows(IllegalArgumentException.class, () -> {
            new Competition("Test", "Desc", model, null, deadline);
        });
    }

    @Test
    @DisplayName("Should throw exception when deadline is null")
    void shouldThrowExceptionWhenDeadlineIsNull() {
        EvaluationModel model = new EvaluationModel();
        User creator = new User();

        assertThrows(IllegalArgumentException.class, () -> {
            new Competition("Test", "Desc", model, creator, null);
        });
    }

    @Test
    @DisplayName("Should set and get all fields")
    void shouldSetAndGetAllFields() {
        Competition competition = new Competition();
        LocalDateTime now = LocalDateTime.now();
        EvaluationModel model = new EvaluationModel();
        User creator = new User();

        competition.setId(1L);
        competition.setName("Test Name");
        competition.setDescription("Test Description");
        competition.setModel(model);
        competition.setCreator(creator);
        competition.setDeadline(now.plusDays(7));
        competition.setStatus(Competition.CompetitionStatus.ACTIVE);
        competition.setCreatedAt(now);
        competition.setUpdatedAt(now);

        assertEquals(1L, competition.getId());
        assertEquals("Test Name", competition.getName());
        assertEquals("Test Description", competition.getDescription());
        assertEquals(model, competition.getModel());
        assertEquals(creator, competition.getCreator());
        assertEquals(Competition.CompetitionStatus.ACTIVE, competition.getStatus());
    }

    @Test
    @DisplayName("Should manage judges collection")
    void shouldManageJudgesCollection() {
        Competition competition = new Competition();
        CompetitionJudge judge1 = new CompetitionJudge();
        CompetitionJudge judge2 = new CompetitionJudge();

        competition.setJudges(Arrays.asList(judge1, judge2));

        assertNotNull(competition.getJudges());
        assertEquals(2, competition.getJudges().size());
    }

    @Test
    @DisplayName("Should manage entries collection")
    void shouldManageEntriesCollection() {
        Competition competition = new Competition();
        CompetitionEntry entry1 = new CompetitionEntry();
        CompetitionEntry entry2 = new CompetitionEntry();

        competition.setEntries(Arrays.asList(entry1, entry2));

        assertNotNull(competition.getEntries());
        assertEquals(2, competition.getEntries().size());
    }

    @Test
    @DisplayName("Should check if competition is active")
    void shouldCheckIfCompetitionIsActive() {
        Competition activeCompetition = new Competition();
        activeCompetition.setStatus(Competition.CompetitionStatus.ACTIVE);

        Competition endedCompetition = new Competition();
        endedCompetition.setStatus(Competition.CompetitionStatus.ENDED);

        assertTrue(activeCompetition.isActive());
        assertFalse(endedCompetition.isActive());
    }

    @Test
    @DisplayName("Should check if competition is ended")
    void shouldCheckIfCompetitionIsEnded() {
        Competition endedCompetition = new Competition();
        endedCompetition.setStatus(Competition.CompetitionStatus.ENDED);

        Competition activeCompetition = new Competition();
        activeCompetition.setStatus(Competition.CompetitionStatus.ACTIVE);

        assertTrue(endedCompetition.isEnded());
        assertFalse(activeCompetition.isEnded());
    }

    @Test
    @DisplayName("Should check if deadline has passed")
    void shouldCheckIfDeadlineHasPassed() {
        Competition pastCompetition = new Competition();
        pastCompetition.setDeadline(LocalDateTime.now().minusDays(1));

        Competition futureCompetition = new Competition();
        futureCompetition.setDeadline(LocalDateTime.now().plusDays(7));

        assertTrue(pastCompetition.isDeadlinePassed());
        assertFalse(futureCompetition.isDeadlinePassed());
    }

    @Test
    @DisplayName("Should determine if competition can accept ratings")
    void shouldDetermineIfCanAcceptRatings() {
        Competition activeFuture = new Competition();
        activeFuture.setStatus(Competition.CompetitionStatus.ACTIVE);
        activeFuture.setDeadline(LocalDateTime.now().plusDays(7));

        Competition activePast = new Competition();
        activePast.setStatus(Competition.CompetitionStatus.ACTIVE);
        activePast.setDeadline(LocalDateTime.now().minusDays(1));

        assertTrue(activeFuture.canAcceptRatings());
        assertFalse(activePast.canAcceptRatings());
    }

    @Test
    @DisplayName("Should determine if competition can accept submissions")
    void shouldDetermineIfCanAcceptSubmissions() {
        Competition activeFuture = new Competition();
        activeFuture.setStatus(Competition.CompetitionStatus.ACTIVE);
        activeFuture.setDeadline(LocalDateTime.now().plusDays(7));

        Competition ended = new Competition();
        ended.setStatus(Competition.CompetitionStatus.ENDED);
        ended.setDeadline(LocalDateTime.now().plusDays(7));

        assertTrue(activeFuture.canAcceptSubmissions());
        assertFalse(ended.canAcceptSubmissions());
    }

    @Test
    @DisplayName("Should handle soft delete")
    void shouldHandleSoftDelete() {
        Competition competition = new Competition();
        assertFalse(competition.isDeleted());

        competition.setDeletedAt(LocalDateTime.now());
        assertTrue(competition.isDeleted());
    }

    @Test
    @DisplayName("Should handle CompetitionStatus enum")
    void shouldHandleCompetitionStatusEnum() {
        assertNotNull(Competition.CompetitionStatus.ACTIVE);
        assertNotNull(Competition.CompetitionStatus.ENDED);
    }
}
