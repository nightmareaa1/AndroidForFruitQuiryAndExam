package com.example.userauth.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

public class ModelRequest {
    
    @NotBlank(message = "模型名称不能为空")
    @Size(max = 100, message = "模型名称长度不能超过100个字符")
    private String name;
    
    @NotEmpty(message = "评价参数不能为空")
    @Valid
    private List<ParameterRequest> parameters;
    
    // Default constructor
    public ModelRequest() {}
    
    // Constructor
    public ModelRequest(String name, List<ParameterRequest> parameters) {
        this.name = name;
        this.parameters = parameters;
    }
    
    // Getters and setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public List<ParameterRequest> getParameters() {
        return parameters;
    }
    
    public void setParameters(List<ParameterRequest> parameters) {
        this.parameters = parameters;
    }
    
    public static class ParameterRequest {
        
        @NotBlank(message = "参数名称不能为空")
        @Size(max = 100, message = "参数名称长度不能超过100个字符")
        private String name;
        
        @jakarta.validation.constraints.NotNull(message = "参数权重不能为空")
        @jakarta.validation.constraints.Min(value = 1, message = "参数权重必须大于0")
        @jakarta.validation.constraints.Max(value = 100, message = "参数权重不能超过100")
        private Integer weight;
        
        // Default constructor
        public ParameterRequest() {}
        
        // Constructor
        public ParameterRequest(String name, Integer weight) {
            this.name = name;
            this.weight = weight;
        }
        
        // Getters and setters
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
    }
}