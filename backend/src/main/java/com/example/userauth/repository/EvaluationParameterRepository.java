package com.example.userauth.repository;

import com.example.userauth.entity.EvaluationParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvaluationParameterRepository extends JpaRepository<EvaluationParameter, Long> {
    
    /**
     * Find parameters by model id ordered by display order
     */
    List<EvaluationParameter> findByModelIdOrderByDisplayOrder(Long modelId);
    
    /**
     * Delete all parameters by model id
     */
    void deleteByModelId(Long modelId);
    
    /**
     * Calculate total weight for a model
     */
    @Query("SELECT COALESCE(SUM(p.weight), 0) FROM EvaluationParameter p WHERE p.model.id = :modelId")
    Integer calculateTotalWeightByModelId(@Param("modelId") Long modelId);
}