package com.example.userauth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class EntryRequest {
    
    @NotBlank(message = "参赛作品名称不能为空")
    @Size(max = 100, message = "参赛作品名称长度不能超过100个字符")
    private String entryName;
    
    @Size(max = 1000, message = "参赛作品描述长度不能超过1000个字符")
    private String description;
    
    // Default constructor
    public EntryRequest() {}
    
    // Constructor
    public EntryRequest(String entryName, String description) {
        this.entryName = entryName;
        this.description = description;
    }
    
    // Getters and setters
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
}