package com.example.userauth.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "competitions")
public class Competition {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_id", nullable = false)
    private EvaluationModel model;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;
    
    @Column(name = "deadline", nullable = false)
    private LocalDateTime deadline;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private CompetitionStatus status = CompetitionStatus.ACTIVE;
    
    @OneToMany(mappedBy = "competition", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CompetitionJudge> judges;
    
    @OneToMany(mappedBy = "competition", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CompetitionEntry> entries;
    
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
    public Competition() {}
    
    // Constructor with required fields
    public Competition(String name, String description, EvaluationModel model, User creator, LocalDateTime deadline) {
        if (model == null) {
            throw new IllegalArgumentException("Competition must be associated with an evaluation model");
        }
        if (creator == null) {
            throw new IllegalArgumentException("Competition must have a creator");
        }
        if (deadline == null) {
            throw new IllegalArgumentException("Competition must have a deadline");
        }
        
        this.name = name;
        this.description = description;
        this.model = model;
        this.creator = creator;
        this.deadline = deadline;
        this.status = CompetitionStatus.ACTIVE;
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
    
    public EvaluationModel getModel() {
        return model;
    }
    
    public void setModel(EvaluationModel model) {
        this.model = model;
    }
    
    public User getCreator() {
        return creator;
    }
    
    public void setCreator(User creator) {
        this.creator = creator;
    }
    
    public LocalDateTime getDeadline() {
        return deadline;
    }
    
    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }
    
    public CompetitionStatus getStatus() {
        return status;
    }
    
    public void setStatus(CompetitionStatus status) {
        this.status = status;
    }
    
    public List<CompetitionJudge> getJudges() {
        return judges;
    }
    
    public void setJudges(List<CompetitionJudge> judges) {
        this.judges = judges;
    }
    
    public List<CompetitionEntry> getEntries() {
        return entries;
    }
    
    public void setEntries(List<CompetitionEntry> entries) {
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
    
    // Convenience methods
    public boolean isActive() {
        return CompetitionStatus.ACTIVE.equals(status);
    }
    
    public boolean isEnded() {
        return CompetitionStatus.ENDED.equals(status);
    }
    
    public boolean isDeadlinePassed() {
        return LocalDateTime.now().isAfter(deadline);
    }
    
    /**
     * Determines if this competition can accept new ratings.
     * A competition can accept ratings if it's active and the deadline hasn't passed.
     */
    public boolean canAcceptRatings() {
        return isActive() && !isDeadlinePassed();
    }
    
    /**
     * Determines if this competition can accept new submissions.
     * A competition can accept submissions if it's active and the deadline hasn't passed.
     */
    public boolean canAcceptSubmissions() {
        return isActive() && !isDeadlinePassed();
    }
    
    public enum CompetitionStatus {
        ACTIVE, ENDED
    }
}