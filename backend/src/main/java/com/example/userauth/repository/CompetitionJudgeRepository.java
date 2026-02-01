package com.example.userauth.repository;

import com.example.userauth.entity.CompetitionJudge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompetitionJudgeRepository extends JpaRepository<CompetitionJudge, Long> {
    
    /**
     * Find all judges for a specific competition
     */
    @Query("SELECT cj FROM CompetitionJudge cj JOIN FETCH cj.judge WHERE cj.competition.id = :competitionId")
    List<CompetitionJudge> findByCompetitionIdWithJudge(@Param("competitionId") Long competitionId);
    
    /**
     * Find all competitions where a user is a judge
     */
    List<CompetitionJudge> findByJudgeId(Long judgeId);
    
    /**
     * Check if a user is already a judge for a competition
     */
    boolean existsByCompetitionIdAndJudgeId(Long competitionId, Long judgeId);
    
    /**
     * Find specific judge assignment
     */
    Optional<CompetitionJudge> findByCompetitionIdAndJudgeId(Long competitionId, Long judgeId);
    
    /**
     * Delete judge assignment
     */
    void deleteByCompetitionIdAndJudgeId(Long competitionId, Long judgeId);
    
    /**
     * Count judges for a competition
     */
    long countByCompetitionId(Long competitionId);
}