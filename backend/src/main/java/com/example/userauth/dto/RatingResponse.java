package com.example.userauth.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class RatingResponse {
    
    private Long id;
    private Long competitionId;
    private Long entryId;
    private String entryName;
    private Long judgeId;
    private String judgeName;
    private List<ScoreResponse> scores;
    private String note;
    private LocalDateTime submittedAt;
    
    // Default constructor
    public RatingResponse() {}
    
    // Constructor
    public RatingResponse(Long id, Long competitionId, Long entryId, String entryName,
                         Long judgeId, String judgeName, List<ScoreResponse> scores,
                         String note, LocalDateTime submittedAt) {
        this.id = id;
        this.competitionId = competitionId;
        this.entryId = entryId;
        this.entryName = entryName;
        this.judgeId = judgeId;
        this.judgeName = judgeName;
        this.scores = scores;
        this.note = note;
        this.submittedAt = submittedAt;
    }
    
    // Getters and setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getCompetitionId() {
        return competitionId;
    }
    
    public void setCompetitionId(Long competitionId) {
        this.competitionId = competitionId;
    }
    
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
    
    public Long getJudgeId() {
        return judgeId;
    }
    
    public void setJudgeId(Long judgeId) {
        this.judgeId = judgeId;
    }
    
    public String getJudgeName() {
        return judgeName;
    }
    
    public void setJudgeName(String judgeName) {
        this.judgeName = judgeName;
    }
    
    public List<ScoreResponse> getScores() {
        return scores;
    }
    
    public void setScores(List<ScoreResponse> scores) {
        this.scores = scores;
    }
    
    public String getNote() {
        return note;
    }
    
    public void setNote(String note) {
        this.note = note;
    }
    
    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }
    
    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }
    
    public static class ScoreResponse {
        private Long parameterId;
        private String parameterName;
        private Integer parameterWeight;
        private BigDecimal score;

        // Default constructor
        public ScoreResponse() {}

        // Constructor
        public ScoreResponse(Long parameterId, String parameterName, Integer parameterWeight, BigDecimal score) {
            this.parameterId = parameterId;
            this.parameterName = parameterName;
            this.parameterWeight = parameterWeight;
            this.score = score;
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

        public BigDecimal getScore() {
            return score;
        }

        public void setScore(BigDecimal score) {
            this.score = score;
        }
    }
}