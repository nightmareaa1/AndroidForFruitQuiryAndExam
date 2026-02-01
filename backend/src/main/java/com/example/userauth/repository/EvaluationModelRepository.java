package com.example.userauth.repository;

import com.example.userauth.entity.EvaluationModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EvaluationModelRepository extends JpaRepository<EvaluationModel, Long> {
    
    /**
     * Find model by name
     */
    Optional<EvaluationModel> findByName(String name);
    
    /**
     * Find all models with their parameters
     */
    @Query("SELECT DISTINCT m FROM EvaluationModel m LEFT JOIN FETCH m.parameters p ORDER BY m.id, p.displayOrder")
    List<EvaluationModel> findAllWithParameters();
    
    /**
     * Find model by id with parameters
     */
    @Query("SELECT m FROM EvaluationModel m LEFT JOIN FETCH m.parameters p WHERE m.id = :id ORDER BY p.displayOrder")
    Optional<EvaluationModel> findByIdWithParameters(@Param("id") Long id);
    
    /**
     * Check if model is used by any competitions
     */
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Competition c WHERE c.model.id = :modelId")
    boolean isModelUsedByCompetitions(@Param("modelId") Long modelId);
}