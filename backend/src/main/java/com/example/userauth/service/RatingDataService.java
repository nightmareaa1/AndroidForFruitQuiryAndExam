package com.example.userauth.service;

import com.example.userauth.dto.CompetitionRatingDataResponse;
import com.example.userauth.entity.*;
import com.example.userauth.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for rating data aggregation and export
 */
@Service
@Transactional(readOnly = true)
public class RatingDataService {
    
    @Autowired
    private CompetitionRepository competitionRepository;
    
    @Autowired
    private CompetitionEntryRepository entryRepository;
    
    @Autowired
    private CompetitionRatingRepository ratingRepository;
    
    @Autowired
    private CompetitionJudgeRepository judgeRepository;
    
    @Autowired
    private EvaluationParameterRepository parameterRepository;
    
    /**
     * Get aggregated rating data for a competition
     */
    public CompetitionRatingDataResponse getCompetitionRatingData(Long competitionId) {
        Competition competition = competitionRepository.findById(competitionId)
                .orElseThrow(() -> new IllegalArgumentException("赛事不存在"));

        List<CompetitionEntry> entries = entryRepository.findByCompetitionIdAndStatusOrderByDisplayOrder(
                competitionId, CompetitionEntry.EntryStatus.APPROVED);

        long totalJudges = judgeRepository.countByCompetitionId(competitionId);

        List<CompetitionRatingDataResponse.EntryRatingData> entryDataList = new ArrayList<>();

        for (CompetitionEntry entry : entries) {
            CompetitionRatingDataResponse.EntryRatingData entryData = calculateEntryRatingData(entry, (int) totalJudges);
            entryDataList.add(entryData);
        }

        return new CompetitionRatingDataResponse(
            competition.getId(),
            competition.getName(),
            competition.getModel() != null ? competition.getModel().getId() : null,
            entryDataList
        );
    }
    
    /**
     * Calculate rating data for a single entry
     */
    private CompetitionRatingDataResponse.EntryRatingData calculateEntryRatingData(CompetitionEntry entry, int totalJudges) {
        // Get detailed average scores for this entry
        List<Object[]> averageScores = ratingRepository.findDetailedAverageScoresByEntryId(entry.getId());
        
        List<CompetitionRatingDataResponse.ParameterAverageScore> parameterScores = new ArrayList<>();
        double totalWeightedScore = 0.0;
        int totalWeight = 0;
        int completedRatings = 0;
        
        for (Object[] row : averageScores) {
            Long parameterId = (Long) row[0];
            String parameterName = (String) row[1];
            Integer parameterWeight = (Integer) row[2];
            Double averageScore = (Double) row[3];
            Long ratingCount = (Long) row[4];
            
            CompetitionRatingDataResponse.ParameterAverageScore parameterAverage = 
                new CompetitionRatingDataResponse.ParameterAverageScore(
                    parameterId, parameterName, averageScore, parameterWeight, ratingCount.intValue()
                );
            
            parameterScores.add(parameterAverage);
            
            // Calculate weighted score for total average
            totalWeightedScore += averageScore;
            totalWeight += parameterWeight;
            
            // Count completed ratings
            if (completedRatings == 0) {
                completedRatings = ratingCount.intValue();
            }
        }
        
        // Calculate total average score
        Double averageTotalScore = parameterScores.isEmpty() ? 0.0 : totalWeightedScore;
        
        return new CompetitionRatingDataResponse.EntryRatingData(
            entry.getId(),
            entry.getEntryName(),
            entry.getContestantName(),
            entry.getFilePath(),
            averageTotalScore,
            completedRatings,
            parameterScores
        );
    }
    
    /**
     * Generate CSV export data for a competition
     */
    public String generateCompetitionCSV(Long competitionId) {
        // Validate competition exists
        Competition competition = competitionRepository.findById(competitionId)
                .orElseThrow(() -> new IllegalArgumentException("赛事不存在"));
        
        // Get all ratings for the competition ordered for export
        List<CompetitionRating> ratings = ratingRepository.findByCompetitionIdForExport(competitionId);
        
        if (ratings.isEmpty()) {
            return generateEmptyCSV(competition);
        }
        
        // Get evaluation parameters for header
        List<EvaluationParameter> parameters = parameterRepository
                .findByModelIdOrderByDisplayOrder(competition.getModel().getId());
        
        StringWriter writer = new StringWriter();
        
        // Write CSV header
        writer.append("参赛作品,评委,");
        for (EvaluationParameter parameter : parameters) {
            writer.append(parameter.getName()).append("(").append(String.valueOf(parameter.getWeight())).append("分),");
        }
        writer.append("总分,备注\n");
        
        // Group ratings by entry and judge
        Map<String, List<CompetitionRating>> groupedRatings = ratings.stream()
                .collect(Collectors.groupingBy(r -> r.getEntry().getId() + "_" + r.getJudge().getId()));
        
        // Write data rows
        for (List<CompetitionRating> ratingGroup : groupedRatings.values()) {
            if (!ratingGroup.isEmpty()) {
                CompetitionRating firstRating = ratingGroup.get(0);
                
                writer.append("\"").append(firstRating.getEntry().getEntryName()).append("\",");
                writer.append("\"").append(firstRating.getJudge().getUsername()).append("\",");
                
                // Create a map of parameter scores for this judge's rating
                Map<Long, Double> parameterScores = ratingGroup.stream()
                        .collect(Collectors.toMap(r -> r.getParameter().getId(), CompetitionRating::getScore));
                
                double totalScore = 0.0;
                
                // Write scores for each parameter in order
                for (EvaluationParameter parameter : parameters) {
                    Double score = parameterScores.get(parameter.getId());
                    if (score != null) {
                        writer.append(String.valueOf(score));
                        totalScore += score;
                    } else {
                        writer.append("0");
                    }
                    writer.append(",");
                }
                
                // Write total score
                writer.append(String.valueOf(totalScore)).append(",");
                
                // Write note (escape quotes)
                String note = firstRating.getNote();
                if (note != null && !note.trim().isEmpty()) {
                    writer.append("\"").append(note.replace("\"", "\"\"")).append("\"");
                }
                
                writer.append("\n");
            }
        }
        
        return writer.toString();
    }
    
    /**
     * Generate empty CSV when no ratings exist
     */
    private String generateEmptyCSV(Competition competition) {
        List<EvaluationParameter> parameters = parameterRepository
                .findByModelIdOrderByDisplayOrder(competition.getModel().getId());
        
        StringWriter writer = new StringWriter();
        
        // Write CSV header
        writer.append("参赛作品,评委,");
        for (EvaluationParameter parameter : parameters) {
            writer.append(parameter.getName()).append("(").append(String.valueOf(parameter.getWeight())).append("分),");
        }
        writer.append("总分,备注\n");
        
        return writer.toString();
    }
    
    /**
     * Check if user has permission to view rating data
     * Rating data is visible to:
     * - Competition creator (admin)
     * - Competition judges
     * - Anyone if competition has ended
     */
    public boolean canViewRatingData(Long competitionId, Long userId) {
        Competition competition = competitionRepository.findById(competitionId)
                .orElseThrow(() -> new IllegalArgumentException("赛事不存在"));
        
        // Competition creator can always view
        if (competition.getCreator().getId().equals(userId)) {
            return true;
        }
        
        // Competition judges can view
        if (judgeRepository.existsByCompetitionIdAndJudgeId(competitionId, userId)) {
            return true;
        }
        
        // If competition has ended, anyone can view
        if (competition.getStatus() == Competition.CompetitionStatus.ENDED) {
            return true;
        }
        
        // Otherwise, unauthorized
        return false;
    }
    
    /**
     * Check if user has permission to export rating data (admin only)
     */
    public boolean canExportRatingData(Long competitionId, Long userId) {
        Competition competition = competitionRepository.findById(competitionId)
                .orElseThrow(() -> new IllegalArgumentException("赛事不存在"));
        
        // Only competition creator (admin) can export
        return competition.getCreator().getId().equals(userId);
    }
}