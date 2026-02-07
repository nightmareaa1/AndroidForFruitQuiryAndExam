package com.example.userauth.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Fruit entity representing a fruit in the nutrition query system.
 * Contains fruit name and related nutrition and flavor data.
 */
@Entity
@Table(name = "fruits")
public class Fruit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    @NotBlank(message = "Fruit name cannot be blank")
    @Size(max = 100, message = "Fruit name must not exceed 100 characters")
    private String name;

    @Column(name = "en_name", nullable = false, length = 100)
    @NotBlank(message = "Fruit English name cannot be blank")
    private String enName;

    @OneToMany(mappedBy = "fruit", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<NutritionData> nutritionData;

    @OneToMany(mappedBy = "fruit", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FlavorData> flavorData;

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

    // Default constructor
    public Fruit() {
    }

    // Constructor with name
    public Fruit(String name) {
        this.name = name;
    }

    // Getters and Setters
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

    public String getEnName() {
        return enName;
    }

    public void setEnName(String enName) {
        this.enName = enName;
    }

    public List<NutritionData> getNutritionData() {
        return nutritionData;
    }

    public void setNutritionData(List<NutritionData> nutritionData) {
        this.nutritionData = nutritionData;
    }

    public List<FlavorData> getFlavorData() {
        return flavorData;
    }

    public void setFlavorData(List<FlavorData> flavorData) {
        this.flavorData = flavorData;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Fruit fruit = (Fruit) o;
        return Objects.equals(id, fruit.id) && Objects.equals(name, fruit.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "Fruit{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}