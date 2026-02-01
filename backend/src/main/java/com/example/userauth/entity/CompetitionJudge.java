package com.example.userauth.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "competition_judges")
public class CompetitionJudge {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competition_id", nullable = false)
    private Competition competition;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "judge_id", nullable = false)
    private User judge;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    // Default constructor
    public CompetitionJudge() {}
    
    // Constructor with required fields
    public CompetitionJudge(Competition competition, User judge) {
        this.competition = competition;
        this.judge = judge;
    }
    
    // Getters and setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Competition getCompetition() {
        return competition;
    }
    
    public void setCompetition(Competition competition) {
        this.competition = competition;
    }
    
    public User getJudge() {
        return judge;
    }
    
    public void setJudge(User judge) {
        this.judge = judge;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}