package com.example.userauth.repository;

import com.example.userauth.entity.NutritionData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for NutritionData entity.
 * Provides data access methods for nutrition data operations.
 */
@Repository
public interface NutritionDataRepository extends JpaRepository<NutritionData, Long> {

    /**
     * Find all nutrition data for a specific fruit by fruit ID.
     * 
     * @param fruitId the fruit ID
     * @return list of nutrition data for the fruit
     */
    List<NutritionData> findByFruitId(Long fruitId);

    /**
     * Find all nutrition data for a specific fruit by fruit name.
     * 
     * @param fruitName the fruit name
     * @return list of nutrition data for the fruit
     */
    List<NutritionData> findByFruitName(String fruitName);
}