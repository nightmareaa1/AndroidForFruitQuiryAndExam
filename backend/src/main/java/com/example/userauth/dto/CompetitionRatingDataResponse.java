package com.example.userauth.dto;

import java.util.List;

/**
 * Response DTO for competition rating data display
 * Matches Android EntryRatingDataDto structure
 */
public class CompetitionRatingDataResponse {

    private Long competitionId;
    private String competitionName;
    private Long modelId;
    private List<EntryRatingData> entries;

    // Default constructor
    public CompetitionRatingDataResponse() {}

    // Constructor
    public CompetitionRatingDataResponse(Long competitionId, String competitionName, Long modelId, List<EntryRatingData> entries) {
        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.modelId = modelId;
        this.entries = entries;
    }
    
    // Getters and setters
    public Long getCompetitionId() {
        return competitionId;
    }
    
    public void setCompetitionId(Long competitionId) {
        this.competitionId = competitionId;
    }
    
    public String getCompetitionName() {
        return competitionName;
    }
    
    public void setCompetitionName(String competitionName) {
        this.competitionName = competitionName;
    }

    public Long getModelId() {
        return modelId;
    }

    public void setModelId(Long modelId) {
        this.modelId = modelId;
    }

    public List<EntryRatingData> getEntries() {
        return entries;
    }
    
    public void setEntries(List<EntryRatingData> entries) {
        this.entries = entries;
    }
    
    /**
     * Rating data for a single entry
     * Field names match Android EntryRatingDataDto
     */
    public static class EntryRatingData {
        private Long entryId;
        private String entryName;
        private String contestantName;
        private String filePath;
        private Double averageTotalScore;
        private Integer numberOfRatings;
        private List<ParameterAverageScore> parameterScores;
        
        // Default constructor
        public EntryRatingData() {}
        
        // Constructor
        public EntryRatingData(Long entryId, String entryName, String contestantName, String filePath,
                              Double averageTotalScore, Integer numberOfRatings,
                              List<ParameterAverageScore> parameterScores) {
            this.entryId = entryId;
            this.entryName = entryName;
            this.contestantName = contestantName;
            this.filePath = filePath;
            this.averageTotalScore = averageTotalScore;
            this.numberOfRatings = numberOfRatings;
            this.parameterScores = parameterScores;
        }
        
        // Getters and setters
        public Long getEntryId() {
            return entryId;
        }
        
        public void setEntryId(Long entryId) {
            this.entryId = entryId;
        }
        
        public String getEntryName() {
            return entryName;
        }
        
        public void setEntryName(String entryName) {
            this.entryName = entryName;
        }
        
        public String getContestantName() {
            return contestantName;
        }
        
        public void setContestantName(String contestantName) {
            this.contestantName = contestantName;
        }
        
        public String getFilePath() {
            return filePath;
        }
        
        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }
        
        public Double getAverageTotalScore() {
            return averageTotalScore;
        }
        
        public void setAverageTotalScore(Double averageTotalScore) {
            this.averageTotalScore = averageTotalScore;
        }
        
        public Integer getNumberOfRatings() {
            return numberOfRatings;
        }
        
        public void setNumberOfRatings(Integer numberOfRatings) {
            this.numberOfRatings = numberOfRatings;
        }
        
        public List<ParameterAverageScore> getParameterScores() {
            return parameterScores;
        }
        
        public void setParameterScores(List<ParameterAverageScore> parameterScores) {
            this.parameterScores = parameterScores;
        }
    }
    
    /**
     * Average score for a parameter
     * Field names match Android ParameterAverageScoreDto
     */
    public static class ParameterAverageScore {
        private Long parameterId;
        private String parameterName;
        private Double averageScore;
        private Integer weight;
        private Integer ratingCount;
        
        // Default constructor
        public ParameterAverageScore() {}
        
        // Constructor
        public ParameterAverageScore(Long parameterId, String parameterName, Double averageScore,
                                   Integer weight, Integer ratingCount) {
            this.parameterId = parameterId;
            this.parameterName = parameterName;
            this.averageScore = averageScore;
            this.weight = weight;
            this.ratingCount = ratingCount;
        }
        
        // Getters and setters
        public Long getParameterId() {
            return parameterId;
        }
        
        public void setParameterId(Long parameterId) {
            this.parameterId = parameterId;
        }
        
        public String getParameterName() {
            return parameterName;
        }
        
        public void setParameterName(String parameterName) {
            this.parameterName = parameterName;
        }
        
        public Double getAverageScore() {
            return averageScore;
        }
        
        public void setAverageScore(Double averageScore) {
            this.averageScore = averageScore;
        }
        
        public Integer getWeight() {
            return weight;
        }
        
        public void setWeight(Integer weight) {
            this.weight = weight;
        }
        
        public Integer getRatingCount() {
            return ratingCount;
        }
        
        public void setRatingCount(Integer ratingCount) {
            this.ratingCount = ratingCount;
        }
    }
}