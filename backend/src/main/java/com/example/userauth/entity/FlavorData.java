package com.example.userauth.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * FlavorData entity representing flavor information for a fruit.
 * Contains component name and value for flavor data.
 */
@Entity
@Table(name = "flavor_data")
public class FlavorData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fruit_id", nullable = false)
    @NotNull(message = "Fruit cannot be null")
    private Fruit fruit;

    @Column(name = "component_name", nullable = false, length = 100)
    @NotBlank(message = "Component name cannot be blank")
    @Size(max = 100, message = "Component name must not exceed 100 characters")
    private String componentName;

    @Column(name = "component_value", nullable = false, precision = 10, scale = 2)
    @NotNull(message = "Component value cannot be null")
    private BigDecimal componentValue;

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
    public FlavorData() {
    }

    // Constructor with all required fields
    public FlavorData(Fruit fruit, String componentName, BigDecimal componentValue) {
        this.fruit = fruit;
        this.componentName = componentName;
        this.componentValue = componentValue;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Fruit getFruit() {
        return fruit;
    }

    public void setFruit(Fruit fruit) {
        this.fruit = fruit;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public BigDecimal getComponentValue() {
        return componentValue;
    }

    public void setComponentValue(BigDecimal componentValue) {
        this.componentValue = componentValue;
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
        FlavorData that = (FlavorData) o;
        return Objects.equals(id, that.id) &&
               Objects.equals(componentName, that.componentName) &&
               Objects.equals(componentValue, that.componentValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, componentName, componentValue);
    }

    @Override
    public String toString() {
        return "FlavorData{" +
                "id=" + id +
                ", componentName='" + componentName + '\'' +
                ", componentValue=" + componentValue +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}