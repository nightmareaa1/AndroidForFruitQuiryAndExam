package com.example.userauth.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "competition_entries")
public class CompetitionEntry {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competition_id", nullable = false)
    private Competition competition;
    
    @Column(name = "entry_name", nullable = false, length = 100)
    private String entryName;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "file_path", length = 255)
    private String filePath;
    
    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private EntryStatus status = EntryStatus.PENDING;
    
    @OneToMany(mappedBy = "entry", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CompetitionRating> ratings;
    
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
    public CompetitionEntry() {}
    
    // Constructor with required fields
    public CompetitionEntry(Competition competition, String entryName, String description, 
                           String filePath, Integer displayOrder) {
        this.competition = competition;
        this.entryName = entryName;
        this.description = description;
        this.filePath = filePath;
        this.displayOrder = displayOrder;
        this.status = EntryStatus.PENDING;
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
    
    public String getFilePath() {
        return filePath;
    }
    
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    
    public Integer getDisplayOrder() {
        return displayOrder;
    }
    
    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }
    
    public EntryStatus getStatus() {
        return status;
    }
    
    public void setStatus(EntryStatus status) {
        this.status = status;
    }
    
    public List<CompetitionRating> getRatings() {
        return ratings;
    }
    
    public void setRatings(List<CompetitionRating> ratings) {
        this.ratings = ratings;
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
    public boolean isPending() {
        return EntryStatus.PENDING.equals(status);
    }
    
    public boolean isApproved() {
        return EntryStatus.APPROVED.equals(status);
    }
    
    public boolean isRejected() {
        return EntryStatus.REJECTED.equals(status);
    }
    
    public enum EntryStatus {
        PENDING, APPROVED, REJECTED
    }
}