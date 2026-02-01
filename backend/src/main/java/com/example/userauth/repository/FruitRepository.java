package com.example.userauth.repository;

import com.example.userauth.entity.Fruit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Fruit entity.
 * Provides data access methods for fruit operations.
 */
@Repository
public interface FruitRepository extends JpaRepository<Fruit, Long> {

    /**
     * Find a fruit by its name.
     * 
     * @param name the fruit name
     * @return Optional containing the fruit if found, empty otherwise
     */
    Optional<Fruit> findByName(String name);

    /**
     * Check if a fruit exists by name.
     * 
     * @param name the fruit name
     * @return true if fruit exists, false otherwise
     */
    boolean existsByName(String name);
}