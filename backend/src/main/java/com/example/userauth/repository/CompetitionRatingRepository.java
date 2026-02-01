package com.example.userauth.repository;

import com.example.userauth.entity.CompetitionRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompetitionRatingRepository extends JpaRepository<CompetitionRating, Long> {
    
    /**
     * Find all ratings for a specific competition
     */
    @Query("SELECT r FROM CompetitionRating r " +
           "JOIN FETCH r.entry " +
           "JOIN FETCH r.judge " +
           "JOIN FETCH r.parameter " +
           "WHERE r.competition.id = :competitionId")
    List<CompetitionRating> findByCompetitionIdWithDetails(@Param("competitionId") Long competitionId);
    
    /**
     * Find all ratings for a specific entry
     */
    @Query("SELECT r FROM CompetitionRating r " +
           "JOIN FETCH r.judge " +
           "JOIN FETCH r.parameter " +
           "WHERE r.entry.id = :entryId")
    List<CompetitionRating> findByEntryIdWithDetails(@Param("entryId") Long entryId);
    
    /**
     * Find all ratings by a specific judge for a competition
     */
    List<CompetitionRating> findByCompetitionIdAndJudgeId(Long competitionId, Long judgeId);
    
    /**
     * Find specific rating by entry, judge, and parameter
     */
    Optional<CompetitionRating> findByEntryIdAndJudgeIdAndParameterId(Long entryId, Long judgeId, Long parameterId);
    
    /**
     * Check if a judge has rated all parameters for an entry
     */
    @Query("SELECT COUNT(r) FROM CompetitionRating r WHERE r.entry.id = :entryId AND r.judge.id = :judgeId")
    long countByEntryIdAndJudgeId(@Param("entryId") Long entryId, @Param("judgeId") Long judgeId);
    
    /**
     * Get average scores for all parameters of an entry
     */
    @Query("SELECT r.parameter.id, AVG(r.score) FROM CompetitionRating r " +
           "WHERE r.entry.id = :entryId GROUP BY r.parameter.id")
    List<Object[]> findAverageScoresByEntryId(@Param("entryId") Long entryId);
    
    /**
     * Get detailed average scores with parameter info for an entry
     */
    @Query("SELECT r.parameter.id, r.parameter.name, r.parameter.weight, AVG(r.score), COUNT(r.score) " +
           "FROM CompetitionRating r " +
           "WHERE r.entry.id = :entryId " +
           "GROUP BY r.parameter.id, r.parameter.name, r.parameter.weight " +
           "ORDER BY r.parameter.displayOrder")
    List<Object[]> findDetailedAverageScoresByEntryId(@Param("entryId") Long entryId);
    
    /**
     * Get all ratings for a competition with entry and parameter details for CSV export
     */
    @Query("SELECT r FROM CompetitionRating r " +
           "JOIN FETCH r.entry e " +
           "JOIN FETCH r.judge j " +
           "JOIN FETCH r.parameter p " +
           "WHERE r.competition.id = :competitionId " +
           "ORDER BY e.displayOrder, j.username, p.displayOrder")
    List<CompetitionRating> findByCompetitionIdForExport(@Param("competitionId") Long competitionId);
}