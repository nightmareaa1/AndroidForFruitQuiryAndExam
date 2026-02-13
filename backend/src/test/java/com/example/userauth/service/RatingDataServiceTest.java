package com.example.userauth.service;

import com.example.userauth.dto.CompetitionRatingDataResponse;
import com.example.userauth.entity.*;
import com.example.userauth.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RatingDataServiceTest {
    
    @Mock
    private CompetitionRepository competitionRepository;
    
    @Mock
    private CompetitionEntryRepository entryRepository;
    
    @Mock
    private CompetitionRatingRepository ratingRepository;
    
    @Mock
    private CompetitionJudgeRepository judgeRepository;
    
    @Mock
    private EvaluationParameterRepository parameterRepository;
    
    @InjectMocks
    private RatingDataService ratingDataService;
    
    private Competition competition;
    private CompetitionEntry entry;
    private EvaluationModel model;
    private EvaluationParameter parameter1;
    private EvaluationParameter parameter2;
    private User creator;
    private User judge;
    
    @BeforeEach
    void setUp() {
        // Create test data
        creator = new User("admin", "hashedPassword", true);
        creator.setId(1L);
        
        judge = new User("judge1", "hashedPassword", false);
        judge.setId(2L);
        
        model = new EvaluationModel("Test Model");
        model.setId(1L);
        
        parameter1 = new EvaluationParameter("外观", 30, 1);
        parameter1.setId(1L);
        parameter1.setModel(model);
        
        parameter2 = new EvaluationParameter("风味", 70, 2);
        parameter2.setId(2L);
        parameter2.setModel(model);
        
        competition = new Competition("Test Competition", "Description", model, creator, LocalDateTime.now().plusDays(1));
        competition.setId(1L);
        
        entry = new CompetitionEntry(competition, "Test Entry", "Description", "/path/to/image.jpg", 1);
        entry.setId(1L);
        entry.setStatus(CompetitionEntry.EntryStatus.APPROVED);
    }
    
    @Test
    void getCompetitionRatingData_ShouldReturnAggregatedData() {
        // Arrange
        when(competitionRepository.findById(1L)).thenReturn(Optional.of(competition));
        when(entryRepository.findByCompetitionIdAndStatusOrderByDisplayOrder(1L, CompetitionEntry.EntryStatus.APPROVED))
                .thenReturn(Arrays.asList(entry));
        when(judgeRepository.countByCompetitionId(1L)).thenReturn(2L);
        
        // Mock rating data: parameter1 avg=25.0, parameter2 avg=60.0
        Object[] ratingData1 = {1L, "外观", 30, 25.0, 2L};
        Object[] ratingData2 = {2L, "风味", 70, 60.0, 2L};
        when(ratingRepository.findDetailedAverageScoresByEntryId(1L))
                .thenReturn(Arrays.asList(ratingData1, ratingData2));
        
        // Act
        CompetitionRatingDataResponse result = ratingDataService.getCompetitionRatingData(1L);
        
        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getCompetitionId());
        assertEquals("Test Competition", result.getCompetitionName());
        assertEquals(1, result.getEntries().size());
        
        CompetitionRatingDataResponse.EntryRatingData entryData = result.getEntries().get(0);
        assertEquals(1L, entryData.getEntryId());
        assertEquals("Test Entry", entryData.getEntryName());
        assertEquals(2, entryData.getParameterScores().size());
        assertEquals(85.0, entryData.getAverageTotalScore()); // 25.0 + 60.0
        assertEquals(2, entryData.getNumberOfRatings());
    }
    
    @Test
    void generateCompetitionCSV_ShouldReturnCSVString() {
        // Arrange
        when(competitionRepository.findById(1L)).thenReturn(Optional.of(competition));
        when(parameterRepository.findByModelIdOrderByDisplayOrder(1L))
                .thenReturn(Arrays.asList(parameter1, parameter2));
        
        // Mock rating data
        CompetitionRating rating1 = new CompetitionRating(competition, entry, judge, parameter1, new BigDecimal("25.0"), "Good");
        CompetitionRating rating2 = new CompetitionRating(competition, entry, judge, parameter2, new BigDecimal("60.0"), "Good");
        when(ratingRepository.findByCompetitionIdForExport(1L))
                .thenReturn(Arrays.asList(rating1, rating2));
        
        // Act
        String csvResult = ratingDataService.generateCompetitionCSV(1L);
        
        // Assert
        assertNotNull(csvResult);
        assertTrue(csvResult.contains("参赛作品,评委,外观(30分),风味(70分),总分,备注"));
        assertTrue(csvResult.contains("Test Entry"));
        assertTrue(csvResult.contains("judge1"));
        assertTrue(csvResult.contains("25.0"));
        assertTrue(csvResult.contains("60.0"));
        assertTrue(csvResult.contains("85.0")); // Total score
        assertTrue(csvResult.contains("Good"));
    }
    
    @Test
    void canViewRatingData_CompetitionEnded_ShouldReturnTrue() {
        // Arrange
        Competition endedCompetition = new Competition("Ended Competition", "Description", model, creator, LocalDateTime.now().minusDays(1));
        endedCompetition.setId(1L);
        endedCompetition.setStatus(Competition.CompetitionStatus.ENDED);
        
        when(competitionRepository.findById(1L)).thenReturn(Optional.of(endedCompetition));
        
        // Act
        boolean canView = ratingDataService.canViewRatingData(1L, 999L); // Any user ID
        
        // Assert
        assertTrue(canView);
    }
    
    @Test
    void canViewRatingData_CompetitionCreator_ShouldReturnTrue() {
        // Arrange
        when(competitionRepository.findById(1L)).thenReturn(Optional.of(competition));
        
        // Act
        boolean canView = ratingDataService.canViewRatingData(1L, 1L); // Creator ID
        
        // Assert
        assertTrue(canView);
    }
    
    @Test
    void canViewRatingData_Judge_ShouldReturnTrue() {
        // Arrange
        when(competitionRepository.findById(1L)).thenReturn(Optional.of(competition));
        when(judgeRepository.existsByCompetitionIdAndJudgeId(1L, 2L)).thenReturn(true);
        
        // Act
        boolean canView = ratingDataService.canViewRatingData(1L, 2L); // Judge ID
        
        // Assert
        assertTrue(canView);
    }
    
    @Test
    void canViewRatingData_UnauthorizedUser_ShouldReturnFalse() {
        // Arrange
        when(competitionRepository.findById(1L)).thenReturn(Optional.of(competition));
        when(judgeRepository.existsByCompetitionIdAndJudgeId(1L, 999L)).thenReturn(false);
        
        // Act
        boolean canView = ratingDataService.canViewRatingData(1L, 999L); // Unauthorized user ID
        
        // Assert
        assertFalse(canView);
    }
    
    @Test
    void canExportRatingData_CompetitionCreator_ShouldReturnTrue() {
        // Arrange
        when(competitionRepository.findById(1L)).thenReturn(Optional.of(competition));
        
        // Act
        boolean canExport = ratingDataService.canExportRatingData(1L, 1L); // Creator ID
        
        // Assert
        assertTrue(canExport);
    }
    
    @Test
    void canExportRatingData_NonCreator_ShouldReturnFalse() {
        // Arrange
        when(competitionRepository.findById(1L)).thenReturn(Optional.of(competition));
        
        // Act
        boolean canExport = ratingDataService.canExportRatingData(1L, 2L); // Non-creator ID
        
        // Assert
        assertFalse(canExport);
    }
    
    @Test
    void getCompetitionRatingData_CompetitionNotFound_ShouldThrowException() {
        // Arrange
        when(competitionRepository.findById(999L)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            ratingDataService.getCompetitionRatingData(999L);
        });
    }
}