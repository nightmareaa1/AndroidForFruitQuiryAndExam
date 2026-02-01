package com.example.userauth.dto;

import java.util.List;

/**
 * Response DTO for competition rating data display
 */
public class CompetitionRatingDataResponse {
    
    private Long competitionId;
    private String competitionName;
    private List<EntryRatingData> entries;
    
    // Default constructor
    public CompetitionRatingDataResponse() {}
    
    // Constructor
    public CompetitionRatingDataResponse(Long competitionId, String competitionName, List<EntryRatingData> entries) {
        this.competitionId = competitionId;
        this.competitionName = competitionName;
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
    
    public List<EntryRatingData> getEntries() {
        return entries;
    }
    
    public void setEntries(List<EntryRatingData> entries) {
        this.entries = entries;
    }
    
    /**
     * Rating data for a single entry
     */
    public static class EntryRatingData {
        private Long entryId;
        private String entryName;
        private String description;
        private String filePath;
        private List<ParameterAverageScore> parameterAverages;
        private Double totalAverageScore;
        private Integer totalJudges;
        private Integer completedRatings;
        
        // Default constructor
        public EntryRatingData() {}
        
        // Constructor
        public EntryRatingData(Long entryId, String entryName, String description, String filePath,
                              List<ParameterAverageScore> parameterAverages, Double totalAverageScore,
                              Integer totalJudges, Integer completedRatings) {
            this.entryId = entryId;
            this.entryName = entryName;
            this.description = description;
            this.filePath = filePath;
            this.parameterAverages = parameterAverages;
            this.totalAverageScore = totalAverageScore;
            this.totalJudges = totalJudges;
            this.completedRatings = completedRatings;
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
        
        public String getDescription() {
            return description;
        }
        
        public void setDescription(String description) {
            this.description = description;
        }
        
        public String getFilePath() {
            return filePath;
        }
        
        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }
        
        public List<ParameterAverageScore> getParameterAverages() {
            return parameterAverages;
        }
        
        public void setParameterAverages(List<ParameterAverageScore> parameterAverages) {
            this.parameterAverages = parameterAverages;
        }
        
        public Double getTotalAverageScore() {
            return totalAverageScore;
        }
        
        public void setTotalAverageScore(Double totalAverageScore) {
            this.totalAverageScore = totalAverageScore;
        }
        
        public Integer getTotalJudges() {
            return totalJudges;
        }
        
        public void setTotalJudges(Integer totalJudges) {
            this.totalJudges = totalJudges;
        }
        
        public Integer getCompletedRatings() {
            return completedRatings;
        }
        
        public void setCompletedRatings(Integer completedRatings) {
            this.completedRatings = completedRatings;
        }
    }
    
    /**
     * Average score for a parameter
     */
    public static class ParameterAverageScore {
        private Long parameterId;
        private String parameterName;
        private Integer parameterWeight;
        private Double averageScore;
        private Integer ratingCount;
        
        // Default constructor
        public ParameterAverageScore() {}
        
        // Constructor
        public ParameterAverageScore(Long parameterId, String parameterName, Integer parameterWeight,
                                   Double averageScore, Integer ratingCount) {
            this.parameterId = parameterId;
            this.parameterName = parameterName;
            this.parameterWeight = parameterWeight;
            this.averageScore = averageScore;
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
        
        public Integer getParameterWeight() {
            return parameterWeight;
        }
        
        public void setParameterWeight(Integer parameterWeight) {
            this.parameterWeight = parameterWeight;
        }
        
        public Double getAverageScore() {
            return averageScore;
        }
        
        public void setAverageScore(Double averageScore) {
            this.averageScore = averageScore;
        }
        
        public Integer getRatingCount() {
            return ratingCount;
        }
        
        public void setRatingCount(Integer ratingCount) {
            this.ratingCount = ratingCount;
        }
    }
}