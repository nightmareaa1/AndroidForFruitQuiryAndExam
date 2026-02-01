package com.example.userauth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

public class CompetitionRequest {
    
    @NotBlank(message = "赛事名称不能为空")
    @Size(max = 100, message = "赛事名称长度不能超过100个字符")
    private String name;
    
    @Size(max = 1000, message = "赛事描述长度不能超过1000个字符")
    private String description;
    
    @NotNull(message = "评价模型ID不能为空")
    private Long modelId;
    
    @NotNull(message = "截止时间不能为空")
    private LocalDateTime deadline;
    
    private List<Long> judgeIds;
    
    // Default constructor
    public CompetitionRequest() {}
    
    // Constructor
    public CompetitionRequest(String name, String description, Long modelId, 
                            LocalDateTime deadline, List<Long> judgeIds) {
        this.name = name;
        this.description = description;
        this.modelId = modelId;
        this.deadline = deadline;
        this.judgeIds = judgeIds;
    }
    
    // Getters and setters
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
    
    public LocalDateTime getDeadline() {
        return deadline;
    }
    
    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }
    
    public List<Long> getJudgeIds() {
        return judgeIds;
    }
    
    public void setJudgeIds(List<Long> judgeIds) {
        this.judgeIds = judgeIds;
    }
}