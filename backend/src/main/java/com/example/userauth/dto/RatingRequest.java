package com.example.userauth.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

public class RatingRequest {
    
    @NotNull(message = "赛事ID不能为空")
    private Long competitionId;
    
    @NotNull(message = "参赛作品ID不能为空")
    private Long entryId;
    
    @NotNull(message = "评分不能为空")
    @Size(min = 1, message = "至少需要一个评分")
    private List<ScoreRequest> scores;
    
    @Size(max = 1000, message = "备注长度不能超过1000个字符")
    private String note;
    
    // Default constructor
    public RatingRequest() {}
    
    // Constructor
    public RatingRequest(Long competitionId, Long entryId, List<ScoreRequest> scores, String note) {
        this.competitionId = competitionId;
        this.entryId = entryId;
        this.scores = scores;
        this.note = note;
    }
    
    // Getters and setters
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
    
    public List<ScoreRequest> getScores() {
        return scores;
    }
    
    public void setScores(List<ScoreRequest> scores) {
        this.scores = scores;
    }
    
    public String getNote() {
        return note;
    }
    
    public void setNote(String note) {
        this.note = note;
    }
    
    public static class ScoreRequest {
        @NotNull(message = "参数ID不能为空")
        private Long parameterId;

        @NotNull(message = "评分不能为空")
        private BigDecimal score;

        // Default constructor
        public ScoreRequest() {}

        // Constructor
        public ScoreRequest(Long parameterId, BigDecimal score) {
            this.parameterId = parameterId;
            this.score = score;
        }

        // Getters and setters
        public Long getParameterId() {
            return parameterId;
        }

        public void setParameterId(Long parameterId) {
            this.parameterId = parameterId;
        }

        public BigDecimal getScore() {
            return score;
        }

        public void setScore(BigDecimal score) {
            this.score = score;
        }
    }
}