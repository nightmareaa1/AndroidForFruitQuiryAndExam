package com.example.userauth.service;

import com.example.userauth.dto.RatingRequest;
import com.example.userauth.dto.RatingResponse;
import com.example.userauth.entity.*;
import com.example.userauth.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RatingService Tests")
class RatingServiceTest {

    @Mock
    private CompetitionRatingRepository ratingRepository;

    @Mock
    private CompetitionRepository competitionRepository;

    @Mock
    private CompetitionEntryRepository entryRepository;

    @Mock
    private CompetitionJudgeRepository judgeRepository;

    @Mock
    private EvaluationParameterRepository parameterRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RatingService ratingService;

    private Competition competition;
    private CompetitionEntry entry;
    private User judge;
    private User admin;
    private EvaluationParameter parameter;
    private CompetitionRating rating;

    @BeforeEach
    void setUp() {
        competition = new Competition();
        competition.setId(1L);
        competition.setName("测试赛事");
        competition.setStatus(Competition.CompetitionStatus.ACTIVE);
        competition.setDeadline(LocalDateTime.now().plusDays(7));

        EvaluationModel model = new EvaluationModel();
        model.setId(1L);
        competition.setModel(model);

        entry = new CompetitionEntry();
        entry.setId(1L);
        entry.setEntryName("测试作品");
        entry.setCompetition(competition);
        entry.setStatus(CompetitionEntry.EntryStatus.APPROVED);

        judge = new User();
        judge.setId(1L);
        judge.setUsername("judge1");
        judge.setIsAdmin(false);

        admin = new User();
        admin.setId(2L);
        admin.setUsername("admin");
        admin.setIsAdmin(true);

        parameter = new EvaluationParameter();
        parameter.setId(1L);
        parameter.setName("甜度");
        parameter.setWeight(10);
        parameter.setDisplayOrder(1);

        rating = new CompetitionRating();
        rating.setId(1L);
        rating.setCompetition(competition);
        rating.setEntry(entry);
        rating.setJudge(judge);
        rating.setParameter(parameter);
        rating.setScore(new BigDecimal("8.5"));
        rating.setSubmittedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should submit rating successfully")
    void submitRating_Success() {
        // Given
        RatingRequest request = createValidRatingRequest();

        when(competitionRepository.findById(1L)).thenReturn(Optional.of(competition));
        when(entryRepository.findById(1L)).thenReturn(Optional.of(entry));
        when(userRepository.findById(1L)).thenReturn(Optional.of(judge));
        when(judgeRepository.existsByCompetitionIdAndJudgeId(1L, 1L)).thenReturn(true);
        when(parameterRepository.findByModelIdOrderByDisplayOrder(1L)).thenReturn(Arrays.asList(parameter));
        when(ratingRepository.findByEntryIdAndJudgeIdAndParameterId(1L, 1L, 1L)).thenReturn(Optional.empty());
        when(ratingRepository.saveAll(anyList())).thenReturn(Arrays.asList(rating));

        // When
        RatingResponse response = ratingService.submitRating(request, 1L);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getEntryId());
        verify(ratingRepository).saveAll(anyList());
    }

    @Test
    @DisplayName("Should throw exception when competition not found")
    void submitRating_CompetitionNotFound() {
        // Given
        RatingRequest request = createValidRatingRequest();
        when(competitionRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> ratingService.submitRating(request, 1L));
        assertEquals("赛事不存在", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when competition deadline passed")
    void submitRating_DeadlinePassed() {
        // Given
        competition.setDeadline(LocalDateTime.now().minusDays(1));
        RatingRequest request = createValidRatingRequest();

        when(competitionRepository.findById(1L)).thenReturn(Optional.of(competition));

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> ratingService.submitRating(request, 1L));
        assertEquals("赛事已截止，无法提交评分", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when entry not found")
    void submitRating_EntryNotFound() {
        // Given
        RatingRequest request = createValidRatingRequest();

        when(competitionRepository.findById(1L)).thenReturn(Optional.of(competition));
        when(entryRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> ratingService.submitRating(request, 1L));
        assertEquals("参赛作品不存在", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when entry not approved")
    void submitRating_EntryNotApproved() {
        // Given
        entry.setStatus(CompetitionEntry.EntryStatus.PENDING);
        RatingRequest request = createValidRatingRequest();

        when(competitionRepository.findById(1L)).thenReturn(Optional.of(competition));
        when(entryRepository.findById(1L)).thenReturn(Optional.of(entry));

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> ratingService.submitRating(request, 1L));
        assertEquals("只能为已审核通过的作品评分", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when user is not authorized judge")
    void submitRating_NotAuthorizedJudge() {
        // Given
        RatingRequest request = createValidRatingRequest();

        when(competitionRepository.findById(1L)).thenReturn(Optional.of(competition));
        when(entryRepository.findById(1L)).thenReturn(Optional.of(entry));
        when(userRepository.findById(1L)).thenReturn(Optional.of(judge));
        when(judgeRepository.existsByCompetitionIdAndJudgeId(1L, 1L)).thenReturn(false);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> ratingService.submitRating(request, 1L));
        assertEquals("您不是该赛事的评委", exception.getMessage());
    }

    @Test
    @DisplayName("Should allow admin to submit rating without being judge")
    void submitRating_AdminCanSubmit() {
        // Given
        RatingRequest request = createValidRatingRequest();

        when(competitionRepository.findById(1L)).thenReturn(Optional.of(competition));
        when(entryRepository.findById(1L)).thenReturn(Optional.of(entry));
        when(userRepository.findById(2L)).thenReturn(Optional.of(admin));
        when(parameterRepository.findByModelIdOrderByDisplayOrder(1L)).thenReturn(Arrays.asList(parameter));
        when(ratingRepository.findByEntryIdAndJudgeIdAndParameterId(1L, 2L, 1L)).thenReturn(Optional.empty());
        when(ratingRepository.saveAll(anyList())).thenReturn(Arrays.asList(rating));

        // When
        RatingResponse response = ratingService.submitRating(request, 2L);

        // Then
        assertNotNull(response);
        verify(judgeRepository, atMost(1)).existsByCompetitionIdAndJudgeId(anyLong(), anyLong());
    }

    @Test
    @DisplayName("Should throw exception when score is out of range")
    void submitRating_ScoreOutOfRange() {
        // Given
        RatingRequest request = createValidRatingRequest();
        request.getScores().get(0).setScore(new BigDecimal("15.0"));

        when(competitionRepository.findById(1L)).thenReturn(Optional.of(competition));
        when(entryRepository.findById(1L)).thenReturn(Optional.of(entry));
        when(userRepository.findById(1L)).thenReturn(Optional.of(judge));
        when(judgeRepository.existsByCompetitionIdAndJudgeId(1L, 1L)).thenReturn(true);
        when(parameterRepository.findByModelIdOrderByDisplayOrder(1L)).thenReturn(Arrays.asList(parameter));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> ratingService.submitRating(request, 1L));
        assertTrue(exception.getMessage().contains("评分必须在"));
    }

    @Test
    @DisplayName("Should update existing rating instead of creating new one")
    void submitRating_UpdateExisting() {
        // Given
        RatingRequest request = createValidRatingRequest();
        CompetitionRating existingRating = new CompetitionRating();
        existingRating.setId(1L);
        existingRating.setScore(new BigDecimal("5.0"));
        existingRating.setParameter(parameter);
        existingRating.setCompetition(competition);
        existingRating.setEntry(entry);
        existingRating.setJudge(judge);

        when(competitionRepository.findById(1L)).thenReturn(Optional.of(competition));
        when(entryRepository.findById(1L)).thenReturn(Optional.of(entry));
        when(userRepository.findById(1L)).thenReturn(Optional.of(judge));
        when(judgeRepository.existsByCompetitionIdAndJudgeId(1L, 1L)).thenReturn(true);
        when(parameterRepository.findByModelIdOrderByDisplayOrder(1L)).thenReturn(Arrays.asList(parameter));
        when(ratingRepository.findByEntryIdAndJudgeIdAndParameterId(1L, 1L, 1L))
                .thenReturn(Optional.of(existingRating));
        when(ratingRepository.saveAll(anyList())).thenReturn(Arrays.asList(existingRating));

        // When
        RatingResponse response = ratingService.submitRating(request, 1L);

        // Then
        assertNotNull(response);
        verify(ratingRepository).saveAll(anyList());
    }

    @Test
    @DisplayName("Should get ratings by entry successfully")
    void getRatingsByEntry_Success() {
        // Given
        when(entryRepository.findById(1L)).thenReturn(Optional.of(entry));
        when(ratingRepository.findByEntryIdWithDetails(1L)).thenReturn(Arrays.asList(rating));

        // When
        List<RatingResponse> responses = ratingService.getRatingsByEntry(1L);

        // Then
        assertNotNull(responses);
        assertFalse(responses.isEmpty());
    }

    @Test
    @DisplayName("Should throw exception when entry not found for get ratings")
    void getRatingsByEntry_NotFound() {
        // Given
        when(entryRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> ratingService.getRatingsByEntry(1L));
        assertEquals("参赛作品不存在", exception.getMessage());
    }

    @Test
    @DisplayName("Should get ratings by competition successfully")
    void getRatingsByCompetition_Success() {
        // Given
        when(competitionRepository.findById(1L)).thenReturn(Optional.of(competition));
        when(ratingRepository.findByCompetitionIdWithDetails(1L)).thenReturn(Arrays.asList(rating));

        // When
        List<RatingResponse> responses = ratingService.getRatingsByCompetition(1L);

        // Then
        assertNotNull(responses);
        verify(ratingRepository).findByCompetitionIdWithDetails(1L);
    }

    @Test
    @DisplayName("Should throw exception when competition not found")
    void getRatingsByCompetition_NotFound() {
        // Given
        when(competitionRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> ratingService.getRatingsByCompetition(1L));
        assertEquals("赛事不存在", exception.getMessage());
    }

    @Test
    @DisplayName("Should get ratings by judge successfully")
    void getRatingsByJudge_Success() {
        // Given
        when(judgeRepository.existsByCompetitionIdAndJudgeId(1L, 1L)).thenReturn(true);
        when(ratingRepository.findByCompetitionIdAndJudgeId(1L, 1L)).thenReturn(Arrays.asList(rating));

        // When
        List<RatingResponse> responses = ratingService.getRatingsByJudge(1L, 1L);

        // Then
        assertNotNull(responses);
    }

    @Test
    @DisplayName("Should throw exception when judge not authorized")
    void getRatingsByJudge_NotAuthorized() {
        // Given
        when(judgeRepository.existsByCompetitionIdAndJudgeId(1L, 1L)).thenReturn(false);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> ratingService.getRatingsByJudge(1L, 1L));
        assertEquals("您不是该赛事的评委", exception.getMessage());
    }

    @Test
    @DisplayName("Should return true when judge has completed rating")
    void hasJudgeCompletedRating_True() {
        // Given
        CompetitionEntry entryWithModel = entry;
        when(entryRepository.findByIdWithCompetition(1L)).thenReturn(Optional.of(entryWithModel));
        when(parameterRepository.findByModelIdOrderByDisplayOrder(1L)).thenReturn(Arrays.asList(parameter));
        when(ratingRepository.countByEntryIdAndJudgeId(1L, 1L)).thenReturn(1L);

        // When
        boolean completed = ratingService.hasJudgeCompletedRating(1L, 1L);

        // Then
        assertTrue(completed);
    }

    @Test
    @DisplayName("Should return false when judge has not completed rating")
    void hasJudgeCompletedRating_False() {
        // Given
        when(entryRepository.findByIdWithCompetition(1L)).thenReturn(Optional.of(entry));
        EvaluationParameter param2 = new EvaluationParameter();
        param2.setId(2L);
        when(parameterRepository.findByModelIdOrderByDisplayOrder(1L)).thenReturn(Arrays.asList(parameter, param2));
        when(ratingRepository.countByEntryIdAndJudgeId(1L, 1L)).thenReturn(1L);

        // When
        boolean completed = ratingService.hasJudgeCompletedRating(1L, 1L);

        // Then
        assertFalse(completed);
    }

    @Test
    @DisplayName("Should delete ratings by competition")
    void deleteRatingsByCompetition_Success() {
        // Given
        when(ratingRepository.findByCompetitionIdWithDetails(1L)).thenReturn(Arrays.asList(rating));

        // When
        ratingService.deleteRatingsByCompetition(1L);

        // Then
        verify(ratingRepository).deleteAll(anyList());
    }

    private RatingRequest createValidRatingRequest() {
        RatingRequest request = new RatingRequest();
        request.setCompetitionId(1L);
        request.setEntryId(1L);
        request.setNote("测试评分");

        RatingRequest.ScoreRequest scoreRequest = new RatingRequest.ScoreRequest();
        scoreRequest.setParameterId(1L);
        scoreRequest.setScore(new BigDecimal("8.5"));
        request.setScores(Arrays.asList(scoreRequest));

        return request;
    }
}
