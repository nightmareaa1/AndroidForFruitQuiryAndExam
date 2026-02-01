package com.example.userauth.repository;

import com.example.userauth.entity.FlavorData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for FlavorData entity.
 * Provides data access methods for flavor data operations.
 */
@Repository
public interface FlavorDataRepository extends JpaRepository<FlavorData, Long> {

    /**
     * Find all flavor data for a specific fruit by fruit ID.
     * 
     * @param fruitId the fruit ID
     * @return list of flavor data for the fruit
     */
    List<FlavorData> findByFruitId(Long fruitId);

    /**
     * Find all flavor data for a specific fruit by fruit name.
     * 
     * @param fruitName the fruit name
     * @return list of flavor data for the fruit
     */
    List<FlavorData> findByFruitName(String fruitName);
}