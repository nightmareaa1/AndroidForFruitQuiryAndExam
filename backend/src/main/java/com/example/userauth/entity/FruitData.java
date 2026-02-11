package com.example.userauth.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Map;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Entity
@Table(name = "fruit_data")
public class FruitData {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fruit_name", nullable = false, length = 100)
    private String fruitName;

    @Column(name = "data_type", nullable = false, length = 50)
    private String dataType;

    @Column(name = "data_values", columnDefinition = "JSON")
    private String dataValuesJson;

    @Transient
    private Map<String, Double> dataValues;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFruitName() { return fruitName; }
    public void setFruitName(String fruitName) { this.fruitName = fruitName; }

    public String getDataType() { return dataType; }
    public void setDataType(String dataType) { this.dataType = dataType; }

    public Map<String, Double> getDataValues() {
        if (dataValuesJson == null || dataValuesJson.isEmpty()) {
            return new java.util.HashMap<>();
        }
        try {
            return objectMapper.readValue(dataValuesJson, new TypeReference<Map<String, Double>>() {});
        } catch (JsonProcessingException e) {
            return new java.util.HashMap<>();
        }
    }

    public void setDataValues(Map<String, Double> dataValues) {
        if (dataValues == null) {
            this.dataValuesJson = null;
        } else {
            try {
                this.dataValuesJson = objectMapper.writeValueAsString(dataValues);
            } catch (JsonProcessingException e) {
                this.dataValuesJson = "{}";
            }
        }
    }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
