package com.example.userauth.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "competition_ratings")
public class CompetitionRating {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competition_id", nullable = false)
    private Competition competition;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entry_id", nullable = false)
    private CompetitionEntry entry;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "judge_id", nullable = false)
    private User judge;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parameter_id", nullable = false)
    private EvaluationParameter parameter;
    
    @Column(name = "score", nullable = false)
    private Double score;
    
    @Column(name = "note", columnDefinition = "TEXT")
    private String note;
    
    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @PrePersist
    protected void onCreate() {
        submittedAt = LocalDateTime.now();
    }
    
    // Default constructor
    public CompetitionRating() {}
    
    // Constructor with required fields
    public CompetitionRating(Competition competition, CompetitionEntry entry, User judge, 
                           EvaluationParameter parameter, Double score, String note) {
        this.competition = competition;
        this.entry = entry;
        this.judge = judge;
        this.parameter = parameter;
        this.score = score;
        this.note = note;
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
    
    public CompetitionEntry getEntry() {
        return entry;
    }
    
    public void setEntry(CompetitionEntry entry) {
        this.entry = entry;
    }
    
    public User getJudge() {
        return judge;
    }
    
    public void setJudge(User judge) {
        this.judge = judge;
    }
    
    public EvaluationParameter getParameter() {
        return parameter;
    }
    
    public void setParameter(EvaluationParameter parameter) {
        this.parameter = parameter;
    }
    
    public Double getScore() {
        return score;
    }
    
    public void setScore(Double score) {
        this.score = score;
    }
    
    public String getNote() {
        return note;
    }
    
    public void setNote(String note) {
        this.note = note;
    }
    
    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }
}