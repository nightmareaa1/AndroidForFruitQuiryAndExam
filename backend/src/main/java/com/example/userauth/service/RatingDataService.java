package com.example.userauth.service;
import com.example.userauth.dto.CompetitionRatingDataResponse;
import com.example.userauth.entity.*;
import com.example.userauth.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
    
    public CompetitionRatingDataResponse getCompetitionRatingData(Long competitionId) {
        Competition competition = competitionRepository.findByIdWithCreator(competitionId)
                .orElseThrow(() -> new IllegalArgumentException("赛事不存在"));
        List<CompetitionEntry> entries = entryRepository.findByCompetitionIdAndStatusOrderByDisplayOrder(
                competitionId, CompetitionEntry.EntryStatus.APPROVED);
        int totalJudges = (int) judgeRepository.countByCompetitionId(competitionId);
        List<CompetitionRatingDataResponse.EntryRatingData> entryDataList = new ArrayList<>();
        for (CompetitionEntry entry : entries) {
            CompetitionRatingDataResponse.EntryRatingData entryData = calculateEntryRatingData(entry, totalJudges);
            entryDataList.add(entryData);
        }
        return new CompetitionRatingDataResponse(
            competition.getId(),
            competition.getName(),
            competition.getModel() != null ? competition.getModel().getId() : null,
            totalJudges,
            entryDataList
        );
    }
    
    private CompetitionRatingDataResponse.EntryRatingData calculateEntryRatingData(CompetitionEntry entry, int totalJudges) {
        List<Object[]> averageScores = ratingRepository.findDetailedAverageScoresByEntryId(entry.getId());
        List<Object[]> judgeTotals = ratingRepository.findJudgeTotalScoresByEntryId(entry.getId());
        
        List<CompetitionRatingDataResponse.ParameterAverageScore> parameterScores = new ArrayList<>();
        double totalWeightedScore = 0.0;
        int totalWeight = 0;
        int completedRatings = 0;
        double highestScore = 0.0;
        
        for (Object[] row : averageScores) {
            Long parameterId = (Long) row[0];
            String parameterName = (String) row[1];
            Integer parameterWeight = (Integer) row[2];
            Double averageScore;
            Object scoreObj = row[3];
            if (scoreObj instanceof Double) {
                averageScore = (Double) scoreObj;
            } else if (scoreObj instanceof Number) {
                averageScore = ((Number) scoreObj).doubleValue();
            } else {
                averageScore = 0.0;
            }
            Long ratingCount = (Long) row[4];
            
            CompetitionRatingDataResponse.ParameterAverageScore parameterAverage = 
                new CompetitionRatingDataResponse.ParameterAverageScore(
                    parameterId, parameterName, averageScore, parameterWeight, ratingCount.intValue()
                );
            
            parameterScores.add(parameterAverage);
            totalWeightedScore += averageScore;
            totalWeight += parameterWeight;
            
            if (completedRatings == 0) {
                completedRatings = ratingCount.intValue();
            }
        }
        
        // Calculate highest score from judge totals
        for (Object[] row : judgeTotals) {
            if (row[0] != null) {
                Double judgeTotal;
                Object judgeTotalObj = row[0];
                if (judgeTotalObj instanceof Double) {
                    judgeTotal = (Double) judgeTotalObj;
                } else if (judgeTotalObj instanceof Number) {
                    judgeTotal = ((Number) judgeTotalObj).doubleValue();
                } else {
                    judgeTotal = 0.0;
                }
                if (judgeTotal > highestScore) {
                    highestScore = judgeTotal;
                }
            }
        }
        
        // Calculate weighted average score
        Double averageTotalScore = (totalWeight > 0 && !parameterScores.isEmpty()) 
            ? totalWeightedScore / totalWeight 
            : 0.0;
        
        return new CompetitionRatingDataResponse.EntryRatingData(
            entry.getId(),
            entry.getEntryName(),
            entry.getContestantName(),
            entry.getFilePath(),
            averageTotalScore,
            highestScore,
            completedRatings,
            totalJudges,
            parameterScores
        );
    }
    
    public String generateCompetitionCSV(Long competitionId) {
        // FIXED: 使用 findByIdWithCreator 避免 LazyInitializationException
        Competition competition = competitionRepository.findByIdWithCreator(competitionId)
                .orElseThrow(() -> new IllegalArgumentException("赛事不存在"));
        
        List<CompetitionRating> ratings = ratingRepository.findByCompetitionIdForExport(competitionId);
        
        if (ratings.isEmpty()) {
            return generateEmptyCSV(competition);
        }
        
        List<EvaluationParameter> parameters = parameterRepository
                .findByModelIdOrderByDisplayOrder(competition.getModel().getId());
        
        StringWriter writer = new StringWriter();
        
        writer.append("参赛作品,评委,");
        for (EvaluationParameter parameter : parameters) {
            writer.append(parameter.getName()).append("(").append(String.valueOf(parameter.getWeight())).append("分),");
        }
        writer.append("总分,备注\n");
        
        Map<String, List<CompetitionRating>> groupedRatings = ratings.stream()
                .collect(Collectors.groupingBy(r -> r.getEntry().getId() + "_" + r.getJudge().getId()));
        
        for (List<CompetitionRating> ratingGroup : groupedRatings.values()) {
            if (!ratingGroup.isEmpty()) {
                CompetitionRating firstRating = ratingGroup.get(0);
                
                writer.append("\"").append(firstRating.getEntry().getEntryName()).append("\",");
                writer.append("\"").append(firstRating.getJudge().getUsername()).append("\",");
                
                Map<Long, BigDecimal> parameterScores = ratingGroup.stream()
                        .collect(Collectors.toMap(r -> r.getParameter().getId(), CompetitionRating::getScore));
                BigDecimal totalScore = BigDecimal.ZERO;
                
                for (EvaluationParameter parameter : parameters) {
                    BigDecimal score = parameterScores.get(parameter.getId());
                    if (score != null) {
                        writer.append(score.toString());
                        totalScore = totalScore.add(score);
                    } else {
                        writer.append("0");
                    }
                    writer.append(",");
                }
                writer.append(totalScore.toString()).append(",");
                
                String note = firstRating.getNote();
                if (note != null && !note.trim().isEmpty()) {
                    writer.append("\"").append(note.replace("\"", "\"\"")).append("\"");
                }
                
                writer.append("\n");
            }
        }
        
        return writer.toString();
    }
    
    private String generateEmptyCSV(Competition competition) {
        List<EvaluationParameter> parameters = parameterRepository
                .findByModelIdOrderByDisplayOrder(competition.getModel().getId());
        
        StringWriter writer = new StringWriter();
        
        writer.append("参赛作品,评委,");
        for (EvaluationParameter parameter : parameters) {
            writer.append(parameter.getName()).append("(").append(String.valueOf(parameter.getWeight())).append("分),");
        }
        writer.append("总分,备注\n");
        
        return writer.toString();
    }
    
    public boolean canViewRatingData(Long competitionId, Long userId) {
        // FIXED: 使用 findByIdWithCreator 避免 LazyInitializationException
        Competition competition = competitionRepository.findByIdWithCreator(competitionId)
                .orElseThrow(() -> new IllegalArgumentException("赛事不存在"));
        
        if (competition.getCreator().getId().equals(userId)) {
            return true;
        }
        
        if (judgeRepository.existsByCompetitionIdAndJudgeId(competitionId, userId)) {
            return true;
        }
        
        if (competition.getStatus() == Competition.CompetitionStatus.ENDED) {
            return true;
        }
        
        return false;
    }
    
    public boolean canExportRatingData(Long competitionId, Long userId) {
        // FIXED: 使用 findByIdWithCreator 避免 LazyInitializationException
        Competition competition = competitionRepository.findByIdWithCreator(competitionId)
                .orElseThrow(() -> new IllegalArgumentException("赛事不存在"));
        
        return competition.getCreator().getId().equals(userId);
    }
}