package com.example.userauth.dto;

import java.time.LocalDateTime;
import java.util.List;

public class CompetitionResponse {
    
    private Long id;
    private String name;
    private String description;
    private Long modelId;
    private String modelName;
    private Long creatorId;
    private String creatorUsername;
    private LocalDateTime deadline;
    private String status;
    private List<JudgeResponse> judges;
    private List<EntryResponse> entries;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Default constructor
    public CompetitionResponse() {}
    
    // Constructor
    public CompetitionResponse(Long id, String name, String description, Long modelId, String modelName,
                             Long creatorId, String creatorUsername, LocalDateTime deadline, String status,
                             List<JudgeResponse> judges, List<EntryResponse> entries,
                             LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.modelId = modelId;
        this.modelName = modelName;
        this.creatorId = creatorId;
        this.creatorUsername = creatorUsername;
        this.deadline = deadline;
        this.status = status;
        this.judges = judges;
        this.entries = entries;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Getters and setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Long getModelId() {
        return modelId;
    }
    
    public void setModelId(Long modelId) {
        this.modelId = modelId;
    }
    
    public String getModelName() {
        return modelName;
    }
    
    public void setModelName(String modelName) {
        this.modelName = modelName;
    }
    
    public Long getCreatorId() {
        return creatorId;
    }
    
    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }
    
    public String getCreatorUsername() {
        return creatorUsername;
    }
    
    public void setCreatorUsername(String creatorUsername) {
        this.creatorUsername = creatorUsername;
    }
    
    public LocalDateTime getDeadline() {
        return deadline;
    }
    
    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public List<JudgeResponse> getJudges() {
        return judges;
    }
    
    public void setJudges(List<JudgeResponse> judges) {
        this.judges = judges;
    }
    
    public List<EntryResponse> getEntries() {
        return entries;
    }
    
    public void setEntries(List<EntryResponse> entries) {
        this.entries = entries;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public static class JudgeResponse {
        private Long id;
        private Long userId;
        private String username;
        private LocalDateTime createdAt;
        
        public JudgeResponse() {}
        
        public JudgeResponse(Long id, Long userId, String username, LocalDateTime createdAt) {
            this.id = id;
            this.userId = userId;
            this.username = username;
            this.createdAt = createdAt;
        }
        
        // Getters and setters
        public Long getId() {
            return id;
        }
        
        public void setId(Long id) {
            this.id = id;
        }
        
        public Long getUserId() {
            return userId;
        }
        
        public void setUserId(Long userId) {
            this.userId = userId;
        }
        
        public String getUsername() {
            return username;
        }
        
        public void setUsername(String username) {
            this.username = username;
        }
        
        public LocalDateTime getCreatedAt() {
            return createdAt;
        }
        
        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }
    }
    
    public static class EntryResponse {
        private Long id;
        private String entryName;
        private String description;
        private String filePath;
        private Integer displayOrder;
        private String status;
        private Long contestantId;
        private String contestantName;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public EntryResponse() {}

        public EntryResponse(Long id, String entryName, String description, String filePath,
                           Integer displayOrder, String status, Long contestantId, String contestantName,
                           LocalDateTime createdAt, LocalDateTime updatedAt) {
            this.id = id;
            this.entryName = entryName;
            this.description = description;
            this.filePath = filePath;
            this.displayOrder = displayOrder;
            this.status = status;
            this.contestantId = contestantId;
            this.contestantName = contestantName;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
        }
        
        // Getters and setters
        public Long getId() {
            return id;
        }
        
        public void setId(Long id) {
            this.id = id;
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
        
        public Integer getDisplayOrder() {
            return displayOrder;
        }
        
        public void setDisplayOrder(Integer displayOrder) {
            this.displayOrder = displayOrder;
        }
        
        public String getStatus() {
            return status;
        }
        
        public void setStatus(String status) {
            this.status = status;
        }
        
        public Long getContestantId() {
            return contestantId;
        }

        public void setContestantId(Long contestantId) {
            this.contestantId = contestantId;
        }

        public String getContestantName() {
            return contestantName;
        }

        public void setContestantName(String contestantName) {
            this.contestantName = contestantName;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }
        
        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }
        
        public LocalDateTime getUpdatedAt() {
            return updatedAt;
        }
        
        public void setUpdatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
        }
    }
}