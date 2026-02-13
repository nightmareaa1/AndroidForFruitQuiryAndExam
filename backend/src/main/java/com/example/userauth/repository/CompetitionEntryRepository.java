package com.example.userauth.repository;

import com.example.userauth.entity.CompetitionEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompetitionEntryRepository extends JpaRepository<CompetitionEntry, Long> {
    
    /**
     * Find all entries for a specific competition ordered by display order
     */
    List<CompetitionEntry> findByCompetitionIdOrderByDisplayOrder(Long competitionId);
    
    /**
     * Find all entries for a specific competition with a specific status
     */
    List<CompetitionEntry> findByCompetitionIdAndStatusOrderByDisplayOrder(Long competitionId, CompetitionEntry.EntryStatus status);
    
    /**
     * Find entry by id with competition details
     */
    @Query("SELECT e FROM CompetitionEntry e JOIN FETCH e.competition WHERE e.id = :id")
    Optional<CompetitionEntry> findByIdWithCompetition(@Param("id") Long id);
    
    /**
     * Get the maximum display order for a competition
     */
    @Query("SELECT COALESCE(MAX(e.displayOrder), 0) FROM CompetitionEntry e WHERE e.competition.id = :competitionId")
    Integer findMaxDisplayOrderByCompetitionId(@Param("competitionId") Long competitionId);
    
    /**
     * Count entries by competition and status
     */
    long countByCompetitionIdAndStatus(Long competitionId, CompetitionEntry.EntryStatus status);

    /**
     * Find all non-deleted entries for a competition
     */
    @Query("SELECT e FROM CompetitionEntry e WHERE e.competition.id = :competitionId AND e.deletedAt IS NULL ORDER BY e.displayOrder")
    List<CompetitionEntry> findActiveByCompetitionIdOrderByDisplayOrder(@Param("competitionId") Long competitionId);

    /**
     * Soft delete entry by setting deleted_at
     */
    @Modifying
    @Query("UPDATE CompetitionEntry e SET e.deletedAt = CURRENT_TIMESTAMP WHERE e.id = :id")
    void softDeleteById(@Param("id") Long id);
}