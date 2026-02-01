package com.example.userauth.repository;

import com.example.userauth.entity.Competition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CompetitionRepository extends JpaRepository<Competition, Long> {
    
    /**
     * Find all competitions created by a specific user
     */
    List<Competition> findByCreatorIdOrderByCreatedAtDesc(Long creatorId);
    
    /**
     * Find all competitions where a user is a judge
     */
    @Query("SELECT DISTINCT c FROM Competition c JOIN c.judges j WHERE j.judge.id = :judgeId ORDER BY c.createdAt DESC")
    List<Competition> findByJudgeIdOrderByCreatedAtDesc(@Param("judgeId") Long judgeId);
    
    /**
     * Find competition by id with all related data
     */
    @Query("SELECT c FROM Competition c " +
           "LEFT JOIN FETCH c.model m " +
           "LEFT JOIN FETCH m.parameters " +
           "LEFT JOIN FETCH c.creator " +
           "LEFT JOIN FETCH c.judges j " +
           "LEFT JOIN FETCH j.judge " +
           "LEFT JOIN FETCH c.entries e " +
           "WHERE c.id = :id")
    Optional<Competition> findByIdWithDetails(@Param("id") Long id);
    
    /**
     * Find all competitions that have passed their deadline but are still active
     */
    @Query("SELECT c FROM Competition c WHERE c.deadline < :now AND c.status = 'ACTIVE'")
    List<Competition> findExpiredActiveCompetitions(@Param("now") LocalDateTime now);
    
    /**
     * Check if a model is used by any competitions
     */
    boolean existsByModelId(Long modelId);
    
    /**
     * Find all active competitions
     */
    List<Competition> findByStatusOrderByCreatedAtDesc(Competition.CompetitionStatus status);
}