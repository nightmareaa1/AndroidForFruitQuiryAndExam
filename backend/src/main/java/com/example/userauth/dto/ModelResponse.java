package com.example.userauth.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ModelResponse {
    
    private Long id;
    private String name;
    private List<ParameterResponse> parameters;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Default constructor
    public ModelResponse() {}
    
    // Constructor
    public ModelResponse(Long id, String name, List<ParameterResponse> parameters, 
                        LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.parameters = parameters;
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
    
    public List<ParameterResponse> getParameters() {
        return parameters;
    }
    
    public void setParameters(List<ParameterResponse> parameters) {
        this.parameters = parameters;
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
    
    public static class ParameterResponse {
        
        private Long id;
        private String name;
        private Integer weight;
        private Integer displayOrder;
        
        // Default constructor
        public ParameterResponse() {}
        
        // Constructor
        public ParameterResponse(Long id, String name, Integer weight, Integer displayOrder) {
            this.id = id;
            this.name = name;
            this.weight = weight;
            this.displayOrder = displayOrder;
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
        
        public Integer getWeight() {
            return weight;
        }
        
        public void setWeight(Integer weight) {
            this.weight = weight;
        }
        
        public Integer getDisplayOrder() {
            return displayOrder;
        }
        
        public void setDisplayOrder(Integer displayOrder) {
            this.displayOrder = displayOrder;
        }
    }
}